package com.soma.snackexercise.auth.jwt.filter;

import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/*
Jwt 인증 필터 ("/login" 이외의 URI 요청이 왔을 때 처리하는 필터)
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final Set<String> NO_CHECK_URLS = new HashSet<>(Arrays.asList("/login", "/api/auth/reissue", "/api/auth/logout"));

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (NO_CHECK_URLS.stream().anyMatch(url -> url.equals(request.getRequestURI()))) {
            filterChain.doFilter(request, response);
            return;
        }
        log.info("JwtAuthenticationProcessingFilter 호출");
        String accessToken = jwtService.extractAccessToken(request).orElse(null);

        if (jwtService.isTokenValid(accessToken)) {
            jwtService.extractEmail(accessToken)
                    .ifPresent(email -> memberRepository.findByEmail(email)
                            .ifPresent(jwtService::saveAuthentication));
        }
        filterChain.doFilter(request, response);
    }
}
