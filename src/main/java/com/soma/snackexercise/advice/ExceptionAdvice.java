package com.soma.snackexercise.advice;

import com.soma.snackexercise.exception.ExgroupNotFoundException;
import com.soma.snackexercise.util.response.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.soma.snackexercise.advice.ErrorCode.NOT_FOUND_EXGROUP;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(ExgroupNotFoundException.class)
    public Response exgroupNotFoundExceptionHandler(ExgroupNotFoundException e){
        return Response.failure(NOT_FOUND_EXGROUP);
    }
}
