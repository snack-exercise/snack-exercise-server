package com.soma.snackexercise.exception.member;

public class MemberNameAlreadyExistsException extends RuntimeException {
    public MemberNameAlreadyExistsException(String message) {
        super(message);
    }
}
