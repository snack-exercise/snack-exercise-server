package com.soma.snackexercise.factory.entity;

import com.soma.snackexercise.domain.group.Group;

import java.time.LocalTime;

public class GroupFactory {
    public static Group createExgroup() {
        return Group.builder()
                .name("name")
                .emozi("emozi")
                .color("red")
                .description("desc")
                .maxMemberNum(3)
                .goalRelayNum(10)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .existDays(5)
                .penalty("커피 사기")
                .code("code")
                .checkIntervalTime(10)
                .checkMaxNum(2)
                .build();
    }

    public static Group createExgroup(String name) {
        return Group.builder()
                .name(name)
                .emozi("emozi")
                .color("red")
                .description("desc")
                .maxMemberNum(3)
                .goalRelayNum(10)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .existDays(5)
                .penalty("커피 사기")
                .code("code")
                .checkIntervalTime(10)
                .checkMaxNum(2)
                .build();
    }
}
