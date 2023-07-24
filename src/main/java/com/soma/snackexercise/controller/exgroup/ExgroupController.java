package com.soma.snackexercise.controller.exgroup;


import com.soma.snackexercise.dto.exgroup.request.ExgroupUpdateRequest;
import com.soma.snackexercise.dto.exgroup.request.PostCreateExgroupRequest;
import com.soma.snackexercise.dto.exgroup.response.ExgroupResponse;
import com.soma.snackexercise.service.exgroup.ExgroupService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Exgroup", description = "운동 그룹 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/exgroups")
public class ExgroupController {

    private final ExgroupService exGroupService;

    @Operation(summary = "운동 그룹 생성", description = "하나의 운동 그룹을 생성합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response createGroup(@RequestBody PostCreateExgroupRequest groupCreateRequest, @AuthenticationPrincipal UserDetails loginUser){
        return Response.success(exGroupService.createGroup(groupCreateRequest, loginUser.getUsername()));
    }


    @Operation(summary = "하나의 운동 그룹 조회", description = "하나의 운동 그룹을 조회합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @Parameter(name = "groupId", description = "조회할 운동 그룹 ID")
    @GetMapping("/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public Response findGroup(@PathVariable("groupId") Long groupId){
        return Response.success(exGroupService.findGroup(groupId));
    }

    @Operation(summary = "운동 그룹 수정",
            description = "하나의 운동 그룹을 수정합니다.",
            security = { @SecurityRequirement(name = "bearer-key") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "운동 그룹 수정 성공", content = @Content(schema = @Schema(implementation = ExgroupResponse.class)))
            })
    @Parameter(name = "groupId", description = "수정할 운동 그룹 ID", required = true,  in = ParameterIn.PATH)
    @PutMapping("/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public Response update(@PathVariable Long groupId,
                           @RequestBody ExgroupUpdateRequest request,
                           @AuthenticationPrincipal UserDetails loginUser) {
        return Response.success(exGroupService.update(groupId, loginUser.getUsername(), request));
    }
}
