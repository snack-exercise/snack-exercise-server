package com.soma.snackexercise.advice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.soma.snackexercise.exception.*;
import com.soma.snackexercise.exception.exercise.ExerciseNotFoundException;
import com.soma.snackexercise.exception.group.*;
import com.soma.snackexercise.exception.joinlist.JoinListNotFoundException;
import com.soma.snackexercise.exception.member.FcmTokenEmptyException;
import com.soma.snackexercise.exception.member.MemberNameAlreadyExistsException;
import com.soma.snackexercise.exception.member.MemberNicknameAlreadyExistsException;
import com.soma.snackexercise.exception.member.MemberNotFoundException;
import com.soma.snackexercise.exception.mission.MissionNotFoundException;
import com.soma.snackexercise.util.response.Response;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
        Sentry.captureException(e);
        return Response.failure(TOKEN_EXPIRED_EXCEPTION);
    }


    /*
    Member
     */
    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response memberNotFoundExceptionHandler(MemberNotFoundException e) {
        Sentry.captureException(e);
        return Response.failure(MEMBER_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(MemberNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response memberNameAlreadyExistsExceptionHandler(MemberNameAlreadyExistsException e) {
        Sentry.captureException(e);
        return Response.failure(MEMBER_NAME_ALREADY_EXISTS_EXCEPTION);
    }

    @ExceptionHandler(MemberNicknameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response memberNicknameAlreadyExistsExceptionHandler(MemberNicknameAlreadyExistsException e) {
        Sentry.captureException(e);
        return Response.failure(MEMBER_NICKNAME_ALREADY_EXISTS_EXCEPTION);
    }

    /*
    Group
     */
    @ExceptionHandler(GroupNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response groupNotFoundExceptionHandler(GroupNotFoundException e){
        Sentry.captureException(e);
        return Response.failure(GROUP_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(NotGroupHostException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response notGroupHostExceptionHandler (NotGroupHostException e){
        Sentry.captureException(e);
        return Response.failure(NOT_GROUP_HOST_EXCEPTION);
    }

    @ExceptionHandler(NotGroupMemberException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response notGroupMemberExceptionHandler (NotGroupMemberException e){
        Sentry.captureException(e);
        return Response.failure(NOT_GROUP_MEMBER_EXCEPTION);
    }

    @ExceptionHandler(InvalidGroupCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response invalidGroupCodeExceptionHandler (InvalidGroupCodeException e){
        Sentry.captureException(e);
        return Response.failure(INVALID_GROUP_CODE_EXCEPTION);
    }

    @ExceptionHandler(AlreadyJoinedGroupException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response alreadyJoinedGroupExceptionHandler (AlreadyJoinedGroupException e){
        Sentry.captureException(e);
        return Response.failure(ALREADY_JOINED_GROUP_EXCEPTION);
    }

    @ExceptionHandler(ExceedsKickOutLimitException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response exceedsKickOutLimitExceptionHandler (ExceedsKickOutLimitException e){
        Sentry.captureException(e);
        return Response.failure(EXCEEDS_KICK_OUT_LIMIT_EXCEPTION);
    }

    @ExceptionHandler(InvalidGroupTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response invalidGroupTimeExceptionHandler (InvalidGroupTimeException e){
        Sentry.captureException(e);
        return Response.failure(INVALID_GROUP_TIME_EXCEPTION);
    }

    /*
    JoinList
     */
    @ExceptionHandler(JoinListNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response joinListNotFoundExceptionHandler (JoinListNotFoundException e){
        Sentry.captureException(e);
        return Response.failure(JOIN_LIST_NOT_FOUND_EXCEPTION);
    }

    /*
    Mission
     */
    @ExceptionHandler(MissionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response missionNotFoundExceptionHandler (MissionNotFoundException e){
        Sentry.captureException(e);
        return Response.failure(MISSION_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(NotStartedGroupException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response notStartedGroupExceptionHandler (NotStartedGroupException e){
        Sentry.captureException(e);
        return Response.failure(NOT_STARTED_GROUP_EXCEPTION);
    }

    /*
    Exercise
     */
    @ExceptionHandler(ExerciseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response exerciseNotFoundExceptionHandler (ExerciseNotFoundException e){
        Sentry.captureException(e);
        return Response.failure(EXERCISE_NOT_FOUND_EXCEPTION);
    }

    /*
    Query Param
     */
    @ExceptionHandler(WrongRequestParamException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response wrongRequestParamExceptionHandler(WrongRequestParamException e){
        Sentry.captureException(e);
        return Response.failure(WRONG_QUERY_PARAM_EXCEPTION);
    }

    /*
    FCM Token
     */
    @ExceptionHandler(FcmTokenEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response fcmTokenEmptyExceptionHandler(FcmTokenEmptyException e){
        Sentry.captureException(e);
        return Response.failure(FCM_TOKEN_NOT_FOUND_EXCEPTION);
    }

    /*
    FCM Token
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response fcmTokenEmptyExceptionHandler(MethodArgumentNotValidException e){
        Sentry.captureException(e);
        return Response.failure(NOT_VALID.changeMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }



}
