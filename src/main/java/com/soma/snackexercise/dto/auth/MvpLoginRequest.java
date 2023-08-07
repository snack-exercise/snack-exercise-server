package com.soma.snackexercise.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MvpLoginRequest {
    private String nickname;
    private String fcmToken;
}
