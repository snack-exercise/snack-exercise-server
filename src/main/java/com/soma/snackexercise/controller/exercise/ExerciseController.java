package com.soma.snackexercise.controller.exercise;

import com.soma.snackexercise.dto.exercise.request.ExerciseCreateRequest;
import com.soma.snackexercise.dto.exercise.request.ExerciseUpdateRequest;
import com.soma.snackexercise.dto.exercise.response.ExerciseResponse;
import com.soma.snackexercise.service.exercise.ExerciseService;
import com.soma.snackexercise.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name="Exercise", description = "운동 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    @Operation(summary = "운동 생성",
               description = "하나의 운동을 생성합니다.",
               security = { @SecurityRequirement(name = "bearer-key") },
               responses = {
                    @ApiResponse(responseCode = "200", description = "운동 생성 성공")
               })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response create(@RequestBody ExerciseCreateRequest request) {
        exerciseService.create(request);
        return Response.success();
    }

    @Operation(summary = "하나의 운동 조회",
               description = "하나의 운동을 조회합니다.",
               security = { @SecurityRequirement(name = "bearer-key") },
               responses = {
                @ApiResponse(responseCode = "200", description = "하나의 운동 조회 성공", content = @Content(schema = @Schema(implementation = ExerciseResponse.class))),
                @ApiResponse(responseCode = "404", description = "운동을 찾을 수 없음", content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @Parameter(name = "exerciseId", description = "조회할 운동 ID", required = true, in = ParameterIn.PATH)
    @GetMapping("/{exerciseId}")
    @ResponseStatus(HttpStatus.OK)
    public Response read(@PathVariable Long exerciseId) {
        return Response.success(exerciseService.read(exerciseId));
    }

    @Operation(summary = "운동 수정",
               description = "하나의 운동을 수정합니다.",
               security = { @SecurityRequirement(name = "bearer-key") },
               responses = {
                @ApiResponse(responseCode = "200", description = "운동 수정 성공", content = @Content(schema = @Schema(implementation = ExerciseResponse.class)))
    })
    @Parameter(name = "exerciseId", description = "수정할 운동 ID", required = true,  in = ParameterIn.PATH)
    @PutMapping("/{exerciseId}")
    @ResponseStatus(HttpStatus.OK)
    public Response update(@PathVariable Long exerciseId,
                           @RequestBody ExerciseUpdateRequest request) {
        return Response.success(exerciseService.update(exerciseId, request));
    }

    @Operation(summary = "운동 삭제",
               description = "하나의 운동을 삭제합니다.",
               security = { @SecurityRequirement(name = "bearer-key") },
               responses = {
                @ApiResponse(responseCode = "200", description = "하나의 운동 삭제 성공", content = @Content(schema = @Schema(implementation = Response.class))),
    })
    @Parameter(name = "exerciseId", description = "삭제할 운동 ID", required = true,  in = ParameterIn.PATH)
    @DeleteMapping("/{exerciseId}")
    @ResponseStatus(HttpStatus.OK)
    public Response delete(@PathVariable Long exerciseId) {
        exerciseService.delete(exerciseId);
        return Response.success();
    }
}
