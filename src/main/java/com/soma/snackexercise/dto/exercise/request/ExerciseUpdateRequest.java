package com.soma.snackexercise.dto.exercise.request;

import com.soma.snackexercise.domain.exercise.ExerciseCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExerciseUpdateRequest {
    private String name;
    private ExerciseCategory exerciseCategory;

    private String videoLink;

    private String description;

    private Integer minPerKcal;
}
