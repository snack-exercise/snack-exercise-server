package com.soma.snackexercise.controller.group;


import com.soma.snackexercise.dto.group.request.GroupCreateRequest;
import com.soma.snackexercise.dto.group.request.GroupUpdateRequest;
import com.soma.snackexercise.dto.group.request.JoinFriendGroupRequest;
import com.soma.snackexercise.dto.group.response.GroupResponse;
import com.soma.snackexercise.service.group.GroupService;
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
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "운동 그룹 생성", description = "하나의 운동 그룹을 생성합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response create(@RequestBody GroupCreateRequest request, @AuthenticationPrincipal UserDetails loginUser){
        return Response.success(groupService.create(request, loginUser.getUsername()));
    }


    @Operation(summary = "하나의 운동 그룹 조회", description = "하나의 운동 그룹을 조회합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @Parameter(name = "groupId", description = "조회할 운동 그룹 ID")
    @GetMapping("/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public Response read(@PathVariable("groupId") Long groupId){
        return Response.success(groupService.read(groupId));
    }

    @Operation(summary = "하나의 운동 그룹에 속한 모든 회원 조회", description = "하나의 운동 그룹에 속한 모든 회원을 조회합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @Parameter(name = "groupId", description = "조회할 운동 그룹 ID")
    @GetMapping("/{groupId}/members")
    public Response readAllMembers(@PathVariable("groupId") Long groupId){
        return Response.success(groupService.getAllExgroupMembers(groupId));
    }

    @Operation(summary = "운동 그룹 수정",
            description = "하나의 운동 그룹을 수정합니다.",
            security = { @SecurityRequirement(name = "bearer-key") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "운동 그룹 수정 성공", content = @Content(schema = @Schema(implementation = GroupResponse.class)))
            })
    @Parameter(name = "groupId", description = "수정할 운동 그룹 ID", required = true,  in = ParameterIn.PATH)
    @PatchMapping("/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public Response update(@PathVariable Long groupId,
                           @RequestBody GroupUpdateRequest request,
                           @AuthenticationPrincipal UserDetails loginUser) {
        return Response.success(groupService.update(groupId, loginUser.getUsername(), request));
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
        groupService.deleteMemberByHost(groupId, memberId, loginUser.getUsername());
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
        groupService.leaveGroupByMember(groupId, loginUser.getUsername());
        return Response.success();
    }

    @Operation(summary = "운동 그룹 시작", description = "운동 그룹을 시작합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @Parameter(name = "groupId", description = "시작할 운동 그룹 ID")
    @PatchMapping("/{groupId}/initiation")
    @ResponseStatus(HttpStatus.OK)
    public Response startGroup(@PathVariable("groupId") Long groupId, @AuthenticationPrincipal UserDetails loginUser){
        return Response.success(groupService.startGroup(groupId, loginUser.getUsername()));
    }

    @Operation(summary = "코드로 지인 그룹 가입하기", description = "코드로 그룹을 가입합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping("/join/code")
    @ResponseStatus(HttpStatus.OK)
    public Response joinFriendGroup(@RequestBody JoinFriendGroupRequest request, @AuthenticationPrincipal UserDetails loginUser) {
        groupService.joinFriendGroup(request, loginUser.getUsername());
        return Response.success();
    }

    @Operation(summary = "회원이 현재 가입되어있는 모든 그룹 조회하기", description = "가입된 모든 그룹을 조회합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Response readAllJoinGroups(@AuthenticationPrincipal UserDetails loginUser) {
        return Response.success(groupService.readAllJoinGroups(loginUser.getUsername()));
    }

    @Operation(summary = "회원이 가입했던 종료된 그룹 조회하기", description = "회원이 가입했던 종료된 그룹을 조회합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping("/finish")
    @ResponseStatus(HttpStatus.OK)
    public Response readAllFinishedJoinGroups(@AuthenticationPrincipal UserDetails loginUser) {
        return Response.success(groupService.readAllFinishedJoinGroups(loginUser.getUsername()));
    }
}
