package com.soma.snackexercise.dto.exercise.request;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.exercise.ExerciseCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExerciseCreateRequest {
    private String name;
    private ExerciseCategory exerciseCategory;
    private String videoLink;
    private String description;
    private Integer minPerKcal;

    public static Exercise toEntity(ExerciseCreateRequest request) {
        return Exercise.builder()
                .name(request.getName())
                .exerciseCategory(request.getExerciseCategory())
                .videoLink(request.getVideoLink())
                .description(request.getDescription())
                .minPerKcal(request.getMinPerKcal())
                .build();
    }
}
