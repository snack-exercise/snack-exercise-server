package com.soma.snackexercise.dto.mission.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MissionCancelRequest {
    private Long missionId;
}
