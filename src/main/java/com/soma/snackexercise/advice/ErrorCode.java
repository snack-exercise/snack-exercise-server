package com.soma.snackexercise.advice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /* Authorization */
    TOKEN_EXPIRED_EXCEPTION(-1000, "토큰이 만료되었습니다."),

    /* Member */
    MEMBER_NOT_FOUND_EXCEPTION(-1100, "사용자가 존재하지 않습니다."),
    MEMBER_NAME_ALREADY_EXISTS_EXCEPTION(-1101, "유저 이름이 이미 존재합니다."),
    MEMBER_NICKNAME_ALREADY_EXISTS_EXCEPTION(-1102, "유저 닉네임이 이미 존재합니다."),

    /* Group */
    // todo : 너무 에러메시지가 친절한가..?
    GROUP_NOT_FOUND_EXCEPTION(-1200, "운동 그룹이 존재하지 않습니다."),
    NOT_GROUP_HOST_EXCEPTION(-1201, "운동 그룹의 방장 권한이 아닙니다."),
    NOT_GROUP_MEMBER_EXCEPTION(-1202, "운동 그룹의 멤버 권한이 아닙니다."),
    INVALID_GROUP_CODE_EXCEPTION(-1203, "잘못된 그룹 코드 입니다."),
    ALREADY_JOINED_GROUP_EXCEPTION(-1204, "이미 해당 운동 그룹에 가입되어 있습니다."),
    EXCEEDS_KICK_OUT_LIMIT_EXCEPTION(-1205, "그룹에 다시 가입할 수 없습니다."),

    /* JoinList */
    JOIN_LIST_NOT_FOUND_EXCEPTION(-1300, "회원_운동그룹이 존재하지 않습니다."),

    /* Mission */
    MISSION_NOT_FOUND_EXCEPTION(-1400, "미션이 존재하지 않습니다."),

    /* QueryParam */
    WRONG_QUERY_PARAM_EXCEPTION(-1900, "잘못된 Query Param입니다.");

    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
}
