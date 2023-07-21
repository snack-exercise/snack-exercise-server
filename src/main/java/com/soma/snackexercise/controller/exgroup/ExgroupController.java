package com.soma.snackexercise.controller.exgroup;


import com.soma.snackexercise.dto.exgroup.request.PostCreateExgroupRequest;
import com.soma.snackexercise.service.exgroup.ExgroupService;
import com.soma.snackexercise.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Exgroup", description = "운동 그룹 API")
@RequiredArgsConstructor
@RestController
public class ExgroupController {

    private final ExgroupService exGroupService;

    @Operation(summary = "운동 그룹 생성", description = "하나의 운동 그룹을 생성합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping("/api/exgroups")
    public Response createGroup(@RequestBody PostCreateExgroupRequest groupCreateRequest, @AuthenticationPrincipal UserDetails loginUser){
        return Response.success(exGroupService.createGroup(groupCreateRequest, loginUser.getUsername()));
    }


    @Operation(summary = "하나의 운동 그룹 조회", description = "하나의 운동 그룹을 조회합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @Parameter(name = "groupId", description = "조회할 운동 그룹 ID")
    @GetMapping("/api/exgroups/{groupId}")
    public Response findGroup(@PathVariable("groupId") Long groupId){
        return Response.success(exGroupService.findGroup(groupId));
    }
}
