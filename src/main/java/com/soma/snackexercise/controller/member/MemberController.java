package com.soma.snackexercise.controller.member;

import com.soma.snackexercise.service.member.MemberService;
import com.soma.snackexercise.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Member", description = "회원 API")
@RequiredArgsConstructor
@RestController()
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }


    @Operation(summary = "하나의 운동 그룹에 속한 모든 회원 조회", description = "하나의 운동 그룹에 속한 모든 회원을 조회합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @Parameter(name = "groupId", description = "조회할 운동 그룹 ID")
    @GetMapping("/exgroups/{groupId}/members")
    public Response getAllExgroupMembers(@PathVariable("groupId") Long groupId){
        return Response.success(memberService.getAllExgroupMembers(groupId));
    }
}
