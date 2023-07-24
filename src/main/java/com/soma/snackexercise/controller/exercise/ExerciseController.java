package com.soma.snackexercise.controller.exercise;

import com.soma.snackexercise.dto.exercise.request.ExerciseCreateRequest;
import com.soma.snackexercise.dto.exercise.request.ExerciseUpdateRequest;
import com.soma.snackexercise.service.exercise.ExerciseService;
import com.soma.snackexercise.util.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response create(@RequestBody ExerciseCreateRequest request) {
        exerciseService.create(request);
        return Response.success();
    }

    @GetMapping("/{id}")
    public Response read(@PathVariable Long id) {
        return Response.success(exerciseService.read(id));
    }

    @PutMapping("/{id}")
    public Response update(@PathVariable Long id,
                           @RequestBody ExerciseUpdateRequest request) {
        return Response.success(exerciseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Long id) {
        exerciseService.delete(id);
        return Response.success();
    }
}
