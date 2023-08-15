package com.soma.snackexercise.dto.mission.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MissionMvpStartRequest {
    private Long exerciseId;
    private Long missionId;
}
