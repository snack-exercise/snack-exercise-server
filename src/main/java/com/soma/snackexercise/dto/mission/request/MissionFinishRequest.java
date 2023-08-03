package com.soma.snackexercise.dto.mission.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MissionFinishRequest {
    private Integer calory; // 소모 칼로리
    private Long lengthOfVideo; // 영상길이
}
