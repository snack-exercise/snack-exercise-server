package com.soma.snackexercise.dto.exercise.response;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.exercise.ExerciseCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponse {
    private Long id;
    private String name;
    private ExerciseCategory exerciseCategory;
    private String videoLink;
    private String description;
    private Integer minPerKcal;

    public static ExerciseResponse toDto(Exercise exercise) {
        return new ExerciseResponse(exercise.getId(),
                exercise.getName(), exercise.getExerciseCategory(),
                exercise.getVideoLink(), exercise.getDescription(),
                exercise.getMinPerKcal());
    }

}
