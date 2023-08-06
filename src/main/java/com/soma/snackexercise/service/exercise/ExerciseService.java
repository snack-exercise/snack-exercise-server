package com.soma.snackexercise.service.exercise;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.dto.exercise.request.ExerciseCreateRequest;
import com.soma.snackexercise.dto.exercise.request.ExerciseUpdateRequest;
import com.soma.snackexercise.dto.exercise.response.ExerciseResponse;
import com.soma.snackexercise.exception.exercise.ExerciseNotFoundException;
import com.soma.snackexercise.repository.exercise.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public void create(ExerciseCreateRequest request) {
        exerciseRepository.save(ExerciseCreateRequest.toEntity(request));
    }


    public ExerciseResponse read(Long id) {
        return ExerciseResponse.toDto(exerciseRepository.findById(id).orElseThrow(ExerciseNotFoundException::new));
    }

    @Transactional
    public ExerciseResponse update(Long id, ExerciseUpdateRequest request) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(ExerciseNotFoundException::new);
        exercise.update(request);
        return ExerciseResponse.toDto(exercise);
    }

    @Transactional
    public void delete(Long id) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(ExerciseNotFoundException::new);
        exercise.inActive();
    }
}
