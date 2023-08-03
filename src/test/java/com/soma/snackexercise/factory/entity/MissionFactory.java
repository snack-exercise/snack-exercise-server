package com.soma.snackexercise.factory.entity;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;

public class MissionFactory {
    public static Mission createNonCompleteMission(Exercise exercise, Member member, Group group) {
        return Mission.builder()
                .exercise(exercise)
                .member(member)
                .group(group)
                .build();
    }

    public static Mission createCompleteMission(Exercise exercise, Member member, Group group) {
        Mission mission = Mission.builder()
                .exercise(exercise)
                .member(member)
                .group(group)
                .build();

        mission.startMission();
        mission.endMission(3, 10L);

        return mission;
    }

    public static Mission createInprogressMission(Exercise exercise, Member member, Group group) {
        Mission mission = Mission.builder()
                .exercise(exercise)
                .member(member)
                .group(group)
                .build();

        mission.startMission();
        return mission;
    }
}
