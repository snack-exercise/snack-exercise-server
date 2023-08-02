package com.soma.snackexercise.dto.mission.response;

import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.dto.exercise.response.ExerciseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MissionResponse {
    private Long id;
    private Integer finishedRelayCount;
    private Integer currentRoundPosition;
    private ExerciseResponse exercise;

    public static MissionResponse toDto(Mission mission, Integer finishedRelayCount, Integer currentRoundPosition) {
        return new MissionResponse(mission.getId(), finishedRelayCount, currentRoundPosition, ExerciseResponse.toDto(mission.getExercise()));
    }
}
