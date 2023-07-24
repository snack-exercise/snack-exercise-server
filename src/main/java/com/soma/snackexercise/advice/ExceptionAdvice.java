package com.soma.snackexercise.advice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.soma.snackexercise.exception.ExgroupNotFoundException;
import com.soma.snackexercise.exception.MemberNameAlreadyExistsException;
import com.soma.snackexercise.exception.WrongRequestParamException;
import com.soma.snackexercise.util.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.soma.snackexercise.advice.ErrorCode.*;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {
    /*
    Authorization
     */
    @ExceptionHandler(TokenExpiredException.class)
    public Response tokenExpiredException(TokenExpiredException e) {
        return Response.failure(TOKEN_EXPIRED_EXCEPTION);
    }


    /*
    Member
     */
    @ExceptionHandler(MemberNameAlreadyExistsException.class)
    public Response memberNameAlreadyExistsException(MemberNameAlreadyExistsException e) {
        return Response.failure(MEMBER_NAME_ALREADY_EXISTS_EXCEPTION);
    }

    /*
    Exgroup
     */
    @ExceptionHandler(ExgroupNotFoundException.class)
    public Response exgroupNotFoundExceptionHandler(ExgroupNotFoundException e){
        return Response.failure(NOT_FOUND_EXGROUP);
    }

    /*
    Query Param
     */
    @ExceptionHandler(WrongRequestParamException.class)
    public Response wrongRequestParamExceptionHandler(WrongRequestParamException e){
        return Response.failure(WRONG_QUERYPARAM);
    }
}
