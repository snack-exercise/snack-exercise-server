package com.soma.snackexercise.factory.entity;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;

public class MissionFactory {
    public static Mission createNonCompleteMission(Exercise exercise, Member member, Exgroup exgroup) {
        return Mission.builder()
                .exercise(exercise)
                .member(member)
                .exgroup(exgroup)
                .build();
    }

    public static Mission createCompleteMission(Exercise exercise, Member member, Exgroup exgroup) {
        Mission mission = Mission.builder()
                .exercise(exercise)
                .member(member)
                .exgroup(exgroup)
                .build();

        mission.startMission();
        mission.endMission(3);

        return mission;
    }

    public static Mission createInprogressMission(Exercise exercise, Member member, Exgroup exgroup) {
        Mission mission = Mission.builder()
                .exercise(exercise)
                .member(member)
                .exgroup(exgroup)
                .build();

        mission.startMission();
        return mission;
    }
}
