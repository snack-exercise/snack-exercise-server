package com.soma.snackexercise.domain.exercise;

import com.soma.snackexercise.domain.BaseEntity;
import com.soma.snackexercise.dto.exercise.request.ExerciseUpdateRequest;
import com.soma.snackexercise.util.constant.Difficulty;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Exercise extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ExerciseCategory exerciseCategory;

    private String emozi;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private String videoLink;

    private String description;

    private Integer minPerKcal;

    public void update(ExerciseUpdateRequest request) {
        this.name = request.getName();
        this.exerciseCategory = request.getExerciseCategory();
        this.videoLink = request.getVideoLink();
        this.description = request.getDescription();
        this.minPerKcal = request.getMinPerKcal();
    }

    @Builder
    public Exercise(String name, ExerciseCategory exerciseCategory, String videoLink, String description, Integer minPerKcal) {
        this.name = name;
        this.exerciseCategory = exerciseCategory;
        this.videoLink = videoLink;
        this.description = description;
        this.minPerKcal = minPerKcal;
        active();
    }
}
