package com.soma.snackexercise.advice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.soma.snackexercise.exception.*;
import com.soma.snackexercise.util.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.soma.snackexercise.advice.ErrorCode.*;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {
    /*
    Authorization
     */
    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response tokenExpiredExceptionHandler(TokenExpiredException e) {
        return Response.failure(TOKEN_EXPIRED_EXCEPTION);
    }


    /*
    Member
     */
    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response memberNotFoundExceptionHandler(MemberNotFoundException e) {
        return Response.failure(MEMBER_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(MemberNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response memberNameAlreadyExistsExceptionHandler(MemberNameAlreadyExistsException e) {
        return Response.failure(MEMBER_NAME_ALREADY_EXISTS_EXCEPTION);
    }

    /*
    Group
     */
    @ExceptionHandler(GroupNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response groupNotFoundExceptionHandler(GroupNotFoundException e){
        return Response.failure(GROUP_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(NotGroupHostException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response notGroupHostExceptionHandler (NotGroupHostException e){
        return Response.failure(NOT_GROUP_HOST_EXCEPTION);
    }

    @ExceptionHandler(NotGroupMemberException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response notGroupMemberExceptionHandler (NotGroupMemberException e){
        return Response.failure(NOT_GROUP_MEMBER_EXCEPTION);
    }

    @ExceptionHandler(InvalidGroupCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response invalidGroupCodeExceptionHandler (InvalidGroupCodeException e){
        return Response.failure(INVALID_GROUP_CODE_EXCEPTION);
    }

    @ExceptionHandler(AlreadyJoinedGroupException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response alreadyJoinedGroupExceptionHandler (AlreadyJoinedGroupException e){
        return Response.failure(ALREADY_JOINED_GROUP_EXCEPTION);
    }

    @ExceptionHandler(ExceedsKickOutLimitException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response exceedsKickOutLimitExceptionHandler (ExceedsKickOutLimitException e){
        return Response.failure(EXCEEDS_KICK_OUT_LIMIT_EXCEPTION);
    }

    /*
    JoinList
     */
    @ExceptionHandler(JoinListNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response joinListNotFoundExceptionHandler (JoinListNotFoundException e){
        return Response.failure(JOIN_LIST_NOT_FOUND_EXCEPTION);
    }

    /*
    Query Param
     */
    @ExceptionHandler(WrongRequestParamException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response wrongRequestParamExceptionHandler(WrongRequestParamException e){
        return Response.failure(WRONG_QUERY_PARAM_EXCEPTION);
    }
}
