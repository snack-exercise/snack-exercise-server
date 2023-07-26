package com.soma.snackexercise.factory.entity;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.exercise.ExerciseCategory;

public class ExerciseFactory {
    public static Exercise createExercise() {
        return Exercise.builder()
                .name("name")
                .exerciseCategory(ExerciseCategory.EXERCISE)
                .videoLink("videoLink")
                .description("description")
                .minPerKcal(10)
                .build();
    }
}
