package com.soma.snackexercise.advice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(-1000),
    MEMBER_NOT_FOUND(-1001),
    MEMBER_NAME_ALREADY_EXISTS(-1002);
    private final int code;
}
