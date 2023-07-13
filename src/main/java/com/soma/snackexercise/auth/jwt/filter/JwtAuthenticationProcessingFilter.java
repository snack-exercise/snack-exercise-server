package com.soma.snackexercise.auth.jwt.filter;

import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.auth.jwt.util.PasswordUtil;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.repository.member.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
Jwt 인증 필터 ("/login" 이외의 URI 요청이 왔을 때 처리하는 필터)

기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청

1. RefreshToken이 없고, AccessToken이 유효 -> 인증 성공, RefreshToken을 재발급하지는 않는다.
2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 Error
3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급, RefreshToken 재발급 (RTR)
                            인증 성공 처리는 하지 않고 실패 처리
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "/login";

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationProcessingFilter 호출");
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
        RefreshToken이 없거나 유효하지 않다면 null 반환
        RefreshToken이 있는 경우, AccessToken이 만료되어 요청한 경우 밖에 없다.
         */
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        /*
        RefreshToken이 존재했다면, RefreshToken이 DB의 RefreshToken과 일치한지 판단 후, 일치하면 AccessToken을 재발급
         */
        if (refreshToken != null) {
            log.info("refreshToken이 있네요");
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        if (refreshToken == null) {
            log.info("refreshToken이 없네요");
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    /*
    [리프레시 토큰의 이메일로 유저 정보 찾기 & 액세스 토큰/리프레시 토큰 재발급 메소드]
    파라미터로 들어온 헤더에서 추출한 리프레시 토큰의 이메일로 DB에서 리프레시 토큰을 찾고,
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        log.info("checkRefreshTokenAndReIssueAccessToken() 호출");
        String email = jwtService.extractEmail(refreshToken).orElse(null);
        Member findMember = memberRepository.findByEmail(email).orElse(null);

        if (email != null && findMember != null) {
            String reIssuedRefreshToken = reIssueRefreshToken(email);
            jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(email), reIssuedRefreshToken);
        }
    }

    /*
    RefreshToken 재발급 후, Redis에 업데이트
     */
    private String reIssueRefreshToken(String email) {
        String reIssuedRefreshToken = jwtService.createRefreshToken(email);
        redisTemplate.opsForValue().set(email, reIssuedRefreshToken);
        return reIssuedRefreshToken;
    }

    /*
    [엑세스 토큰 체크 & 인증 처리 메소드]
    request에서 accessToken 추출 후, 유효한 토큰인지 확인
    AccessToken의 이메일로 유저 객체를 saveAuthentication()으로 인증 처리
    그후 객체를 SecurityContextHolder에 담기
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(accessToken -> jwtService.extractEmail(accessToken)
                        .ifPresent(email -> memberRepository.findByEmail(email)
                                .ifPresent(this::saveAuthentication)));

        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(Member myMember) {
        log.info("saveAuthentication() 호출");
        String password = myMember.getPassword();

        if (password == null) {
            password = PasswordUtil.generateRandomPassword();
        }

        UserDetails userDetailsUser = User.builder()
                .username(myMember.getEmail())
                .password(password)
                .roles(myMember.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
