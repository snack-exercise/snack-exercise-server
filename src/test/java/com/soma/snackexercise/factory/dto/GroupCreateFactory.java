package com.soma.snackexercise.factory.dto;

import com.soma.snackexercise.dto.group.request.GroupCreateRequest;
import com.soma.snackexercise.dto.group.request.JoinFriendGroupRequest;

import java.time.LocalTime;

public class GroupCreateFactory {
    public static GroupCreateRequest createGroupCreateRequest() {
        return new GroupCreateRequest("name", "emozi", "color", "description", 3, 10,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "커피 쏘기", 20, 2, 10);
    }

    public static JoinFriendGroupRequest createJoinFriendGroupRequest() {
        return new JoinFriendGroupRequest("code");
    }

    public static JoinFriendGroupRequest createJoinFriendGroupRequestWithCode(String code) {
        return new JoinFriendGroupRequest(code);
    }
}
