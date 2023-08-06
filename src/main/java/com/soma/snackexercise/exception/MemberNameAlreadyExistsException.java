package com.soma.snackexercise.exception;

public class MemberNameAlreadyExistsException extends RuntimeException {
    public MemberNameAlreadyExistsException(String message) {
        super(message);
    }
}
