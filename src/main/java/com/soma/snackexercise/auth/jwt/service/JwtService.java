package com.soma.snackexercise.auth.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.soma.snackexercise.auth.jwt.util.PasswordUtil;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.exception.ExpiredJwtException;
import com.soma.snackexercise.exception.InvalidRefreshTokenException;
import com.soma.snackexercise.exception.UnauthorizedException;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtDecoderInitializationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    public String createAccessToken(String email) {
        Date now = new Date();

        System.out.println(new Date(now.getTime() + accessTokenExpirationPeriod) + "--------------------------");

        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(EMAIL_CLAIM, email)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String createRefreshToken(String email) {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .withClaim(EMAIL_CLAIM, email)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader, accessToken);
        response.setHeader(refreshHeader, refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    /*
    헤더에서 AccessToken 추출
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    /*
    헤더에서 RefreshToken 추출
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /*
    AccessToken에서 Email 추출
     */
    public Optional<String> extractEmail(String token) {
        try {
            // 토큰 유효성 검사하는 데에 사용할 알고리즘이 있는 JWT verifier builder 반환
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token)
                    .getClaim(EMAIL_CLAIM)
                    .asString());
        } catch (Exception e) {
            log.error("토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    /*
    AccessToken 헤더 설정
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    /*
    RefreshToken 헤더 설정
     */
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    /*
    RefreshToken DB 저장 (업데이트)
     */
    public void updateRefreshToken(String email, String refreshToken) {
        redisUtil.set(email, refreshToken, refreshTokenExpirationPeriod);
    }

    /*
    토큰의 유효성 검사
     */
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);

            if (redisUtil.hasKeyBlackList(token)) {
                throw new UnauthorizedException("이미 로그아웃한 회원입니다");
            }

            return true;
        } catch (SecurityException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (JWTDecodeException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        } catch (UnauthorizedException e) {
            log.info("이미 탈퇴한 회원입니다.");
        } catch (Exception e) {
            log.info("잘못된 JWT 토큰입니다.");
        }
        return false;
    }

    public boolean isRefreshTokenMatch(String refreshToken) {
        String email = extractEmail(refreshToken).orElseThrow(InvalidRefreshTokenException::new);
        if (redisUtil.get(email).equals(refreshToken)) {
            return true;
        }

        throw new InvalidRefreshTokenException();
    }

    public void sendAccessAndRefreshByEmail(HttpServletResponse response, String email) {
        String AT = createAccessToken(email);
        String RT = createRefreshToken(email);
        updateRefreshToken(email, RT);
        sendAccessAndRefreshToken(response, AT, RT);
    }

    public void saveAuthentication(Member myMember) {
        log.info("JwtService.saveAuthentication() 호출");
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
