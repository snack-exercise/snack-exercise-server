package com.soma.snackexercise.advice;

import com.soma.snackexercise.exception.MemberNameAlreadyExistsException;
import com.soma.snackexercise.util.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.soma.snackexercise.advice.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.soma.snackexercise.advice.ErrorCode.MEMBER_NAME_ALREADY_EXISTS;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(Exception e) {
        log.info("e = {}", e.getMessage());
        return Response.failure(INTERNAL_SERVER_ERROR.getCode(), "서버 오류가 발생하였습니다");
    }

    @ExceptionHandler(MemberNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response memberNameAlreadyExistsException(MemberNameAlreadyExistsException e) {
        return Response.failure(MEMBER_NAME_ALREADY_EXISTS.getCode(), e.getMessage() + "은 중복된 이름입니다.");
    }
}
