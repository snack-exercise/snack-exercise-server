package com.soma.snackexercise.service.auth;

import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.exception.InvalidRefreshTokenException;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.exception.UnauthorizedException;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;

    @Transactional(readOnly = true)
    public void reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtService.extractRefreshToken(request).orElse(null);
        String email = jwtService.extractEmail(refreshToken).orElse(null);

        if (!jwtService.isTokenValid(refreshToken) || !refreshToken.equals(redisUtil.get(email))) {
            throw new InvalidRefreshTokenException();
        }

        memberRepository.findByEmail(email).ifPresentOrElse(jwtService::saveAuthentication, MemberNotFoundException::new);

        jwtService.sendAccessAndRefreshByEmail(response, email);
    }

    public void logout(HttpServletRequest request) {
        String accessToken = jwtService.extractAccessToken(request).orElse(null);
        String email = jwtService.extractEmail(accessToken).orElse(null);
        redisUtil.delete(email);
        redisUtil.setBlackList(accessToken, "AccessToken", jwtService.getAccessTokenExpirationPeriod());
    }
}
