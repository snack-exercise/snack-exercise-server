package com.soma.snackexercise.dto.mission.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MissionFinishResponse {
    private Boolean isGroupGoalAchieved; // 그룹 목표 달성 여부
}
