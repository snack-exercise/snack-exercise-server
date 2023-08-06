package com.soma.snackexercise.repository.exercise;

import com.soma.snackexercise.domain.exercise.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
