package com.soma.snackexercise.dto.mission.response;

import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.dto.exercise.response.ExerciseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MissionStartResponse {
    private Long missionId;
    private ExerciseResponse exerciseResponse;

    public static MissionStartResponse toDto(Mission mission) {
        return new MissionStartResponse(mission.getId(), ExerciseResponse.toDto(mission.getExercise()));
    }
}
