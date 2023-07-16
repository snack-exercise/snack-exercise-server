package com.soma.snackexercise.advice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(-1000),
    MEMBER_NAME_ALREADY_EXISTS(-1001);
    private final int code;
}
