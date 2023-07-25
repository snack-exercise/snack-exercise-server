package com.soma.snackexercise.controller.exgroup;


import com.soma.snackexercise.dto.exgroup.request.ExgroupUpdateRequest;
import com.soma.snackexercise.dto.exgroup.request.ExgroupCreateRequest;
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
    public Response createGroup(@RequestBody ExgroupCreateRequest groupCreateRequest, @AuthenticationPrincipal UserDetails loginUser){
        return Response.success(exGroupService.create(groupCreateRequest, loginUser.getUsername()));
    }


    @Operation(summary = "하나의 운동 그룹 조회", description = "하나의 운동 그룹을 조회합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @Parameter(name = "groupId", description = "조회할 운동 그룹 ID")
    @GetMapping("/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public Response findGroup(@PathVariable("groupId") Long groupId){
        return Response.success(exGroupService.findGroup(groupId));
    }

    @Operation(summary = "하나의 운동 그룹에 속한 모든 회원 조회", description = "하나의 운동 그룹에 속한 모든 회원을 조회합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @Parameter(name = "groupId", description = "조회할 운동 그룹 ID")
    @GetMapping("/exgroups/{groupId}/members")
    public Response getAllExgroupMembers(@PathVariable("groupId") Long groupId){
        return Response.success(exGroupService.getAllExgroupMembers(groupId));
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

    @Operation(summary = "운동 그룹에서 방장이 회원 탈퇴",
            description = "운동 그룹에서 방장이 회원을 탈퇴시킵니다.",
            security = { @SecurityRequirement(name = "bearer-key") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 탈퇴 처리 성공", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @Parameter(name = "groupId", description = "운동 그룹 ID", required = true,  in = ParameterIn.PATH)
    @Parameter(name = "memberId", description = "탈퇴시킬 회원 ID", required = true,  in = ParameterIn.PATH)
    @DeleteMapping("/{groupId}/members/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public Response deleteMemberByHost(@PathVariable Long groupId, @PathVariable Long memberId, @AuthenticationPrincipal UserDetails loginUser) {
        exGroupService.deleteMemberByHost(groupId, memberId, loginUser.getUsername());
        return Response.success();
    }

    @Operation(summary = "회원이 운동 그룹 탈퇴",
            description = "회원이 운동 그룹을 탈퇴합니다.",
            security = { @SecurityRequirement(name = "bearer-key") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 탈퇴 처리 성공", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @Parameter(name = "groupId", description = "운동 그룹 ID", required = true,  in = ParameterIn.PATH)
    @DeleteMapping("/{groupId}")
    public Response leaveGroupByMember(@PathVariable Long groupId, @AuthenticationPrincipal UserDetails loginUser) {
        exGroupService.leaveGroupByMember(groupId, loginUser.getUsername());
        return Response.success();
    }

    @Operation(summary = "운동 그룹 시작", description = "운동 그룹을 시작합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @Parameter(name = "groupId", description = "시작할 운동 그룹 ID")
    @PatchMapping("/{groupId}/initiation")
    @ResponseStatus(HttpStatus.OK)
    public Response startGroup(@PathVariable("groupId") Long groupId, @AuthenticationPrincipal UserDetails loginUser){
        return Response.success(exGroupService.startGroup(groupId, loginUser.getUsername()));
    }
}
