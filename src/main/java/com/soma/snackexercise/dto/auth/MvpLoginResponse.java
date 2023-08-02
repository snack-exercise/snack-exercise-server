package com.soma.snackexercise.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MvpLoginResponse {
    private String accessToken;
    private Long id;
}
