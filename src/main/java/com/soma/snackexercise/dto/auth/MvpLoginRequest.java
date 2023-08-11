package com.soma.snackexercise.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MvpLoginRequest {
    private String nickname;

    @NotBlank(message = "fcm 토큰은 필수 값입니다.")
    private String fcmToken;
}
