package com.soma.snackexercise.factory.dto;

import com.soma.snackexercise.dto.group.request.GroupUpdateRequest;

import java.time.LocalTime;

public class GroupUpdateFactory {
    public static GroupUpdateRequest createGroupUpdateRequest() {
        return new GroupUpdateRequest("name", "emozi", "color", "description", 3, 10,
                LocalTime.now(), LocalTime.now().plusHours(1), "커피 쏘기", 20, 2, 10);
    }
}
