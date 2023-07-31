package com.soma.snackexercise.factory.dto;

import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.dto.group.response.GroupResponse;
import com.soma.snackexercise.dto.member.response.GetOneGroupMemberResponse;
import com.soma.snackexercise.util.constant.Status;

import java.time.LocalDate;
import java.time.LocalTime;

public class GroupReadFactory {
    public static GetOneGroupMemberResponse createGetOneGroupMemberResponse() {
        return new GetOneGroupMemberResponse("profileImg", "nickname", JoinType.MEMBER, Status.ACTIVE);
    }

    public static GroupResponse createGroupResponse() {
        return new GroupResponse(1L, "name", "emozi", "color", "description", 3, 10,
                LocalTime.now(), LocalTime.now().plusHours(1), 10, LocalDate.now(), LocalDate.now().plusDays(1),
                "커피 쏘기", "1111", 2, 10);
    }
}
