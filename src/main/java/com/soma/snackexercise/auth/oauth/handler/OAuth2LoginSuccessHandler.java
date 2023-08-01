package com.soma.snackexercise.auth.oauth.handler;

import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.auth.oauth.CustomOAuth2User;
import com.soma.snackexercise.domain.member.Role;
import com.soma.snackexercise.repository.member.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공");

        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            String refreshToken = jwtService.createRefreshToken(oAuth2User.getEmail());
            jwtService.sendRefreshToken(response, refreshToken);

            if (oAuth2User.getRole() == Role.GUEST) {
                response.sendRedirect("http://localhost:3000/signup");

                log.info("새로운 회원가입으로 분기");
            }else{
                response.sendRedirect("http://localhost:3000/login");
                log.info("기존 회원으로 분기");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
