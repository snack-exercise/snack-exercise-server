package com.soma.snackexercise.advice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /* Authorization */
    TOKEN_EXPIRED_EXCEPTION(-1000, "토큰이 만료되었습니다."),

    /* Member */
    MEMBER_NAME_ALREADY_EXISTS_EXCEPTION(-1100, "유저 이름이 이미 존재합니다."),

    /* Exgroup */
    NOT_FOUND_EXGROUP(1200, "운동 그룹이 존재하지 않습니다.");
    
    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
}
