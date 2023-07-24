package com.soma.snackexercise.service.auth;

import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.dto.auth.AccessTokenResponse;
import com.soma.snackexercise.exception.InvalidRefreshTokenException;
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
    private final RedisUtil redisUtil;

    @Transactional(readOnly = true)
    public AccessTokenResponse reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtService.extractRefreshToken(request);
        String email = jwtService.extractEmail(refreshToken).orElseThrow(InvalidRefreshTokenException::new);

        /*
        refreshToken이 유효한지 / DB에 저장된 refreshToken과 일치한지 체크
         */
        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshTokenMatch(email, refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        String newAccessToken = jwtService.createAccessToken(email);
        String newRefreshToken = jwtService.createRefreshToken(email);

        jwtService.sendRefreshToken(response, newRefreshToken);
        jwtService.updateRefreshToken(email, newRefreshToken);

        return new AccessTokenResponse("Bearer " + newAccessToken);
    }

    @Transactional(readOnly = true)
    public void logout(HttpServletRequest request) {
        String accessToken = jwtService.extractAccessToken(request).orElse(null);
        String email = jwtService.extractEmail(accessToken).orElse(null);
        redisUtil.delete(email);
        redisUtil.setBlackList(accessToken, "AccessToken", jwtService.getAccessTokenExpirationPeriod());
    }
}
