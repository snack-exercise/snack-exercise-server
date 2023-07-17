package com.soma.snackexercise.advice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* Exgroup */
    NOT_FOUND_EXGROUP(1200, "운동 그룹이 존재하지 않습니다.");
    
    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
}
