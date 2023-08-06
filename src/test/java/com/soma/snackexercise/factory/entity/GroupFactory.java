package com.soma.snackexercise.factory.entity;

import com.soma.snackexercise.domain.group.Group;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class GroupFactory {
    public static Group createGroup() {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        return Group.builder()
                .name("name")
                .emozi("emozi")
                .color("red")
                .description("desc")
                .maxMemberNum(3)
                .goalRelayNum(10)
                .startTime(now)
                .endTime(now.plusHours(1))
                .existDays(5)
                .penalty("커피 사기")
                .code("code")
                .checkIntervalTime(10)
                .checkMaxNum(2)
                .build();
    }

    public static Group createGroupWithStartTime(LocalTime startTime) {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        return Group.builder()
                .name("name")
                .emozi("emozi")
                .color("red")
                .description("desc")
                .maxMemberNum(3)
                .goalRelayNum(10)
                .startTime(startTime)
                .endTime(startTime.plusHours(1))
                .existDays(5)
                .penalty("커피 사기")
                .code("code")
                .checkIntervalTime(10)
                .checkMaxNum(2)
                .build();
    }

    public static Group createGroupWithName(String name) {
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

    public static Group createGroupWithCode(String code) {
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
                .code(code)
                .checkIntervalTime(10)
                .checkMaxNum(2)
                .build();
    }

    public static Group createGroupWithCheckIntervalTime(Integer checkIntervalTime) {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        return Group.builder()
                .name("name")
                .emozi("emozi")
                .color("red")
                .description("desc")
                .maxMemberNum(3)
                .goalRelayNum(10)
                .startTime(now)
                .endTime(now.plusHours(1))
                .existDays(5)
                .penalty("커피 사기")
                .code("code")
                .checkIntervalTime(checkIntervalTime)
                .checkMaxNum(2)
                .build();
    }

    public static Group createGroupWithExistDays(Integer existDays) {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        return Group.builder()
                .name("name")
                .emozi("emozi")
                .color("red")
                .description("desc")
                .maxMemberNum(3)
                .goalRelayNum(10)
                .startTime(now)
                .endTime(now.plusHours(1))
                .existDays(existDays)
                .penalty("커피 사기")
                .code("code")
                .checkIntervalTime(10)
                .checkMaxNum(2)
                .build();
    }
}
