package com.soma.snackexercise.factory.entity;

import com.soma.snackexercise.domain.exgroup.Exgroup;

import java.time.LocalTime;

public class ExgroupFactory {
    public static Exgroup createExgroup() {
        return Exgroup.builder()
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

    public static Exgroup createExgroup(String name) {
        return Exgroup.builder()
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
