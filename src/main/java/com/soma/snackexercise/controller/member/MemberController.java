package com.soma.snackexercise.controller.member;

import com.soma.snackexercise.dto.member.request.MemberUpdateRequest;
import com.soma.snackexercise.dto.member.response.MemberResponse;
import com.soma.snackexercise.service.member.MemberService;
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


@Tag(name = "Member", description = "회원 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/my")
public class MemberController {
    private final MemberService memberService;


    // TODO : UserId는 민감한 정보이므로 URL에 직접 포함하지 않는 것이 좋은듯
    @Operation(summary = "회원 수정",
            description = "회원 한명을 수정합니다.",
            security = { @SecurityRequirement(name = "bearer-key") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 수정 성공", content = @Content(schema = @Schema(implementation = MemberResponse.class)))
            })
    @Parameter(name = "memberId", description = "수정할 회원 ID", required = true,  in = ParameterIn.PATH)
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Response update(@RequestBody MemberUpdateRequest request, @AuthenticationPrincipal UserDetails loginUser) {
        return Response.success(memberService.update(loginUser.getUsername(), request));
    }

    @Operation(summary = "회원 탈퇴",
            description = "한명의 회원을 삭제합니다.",
            security = { @SecurityRequirement(name = "bearer-key") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "한명의 회원 삭제 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            })
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public Response delete(@AuthenticationPrincipal UserDetails loginUser) {
        memberService.delete(loginUser.getUsername());
        return Response.success();
    }
}
