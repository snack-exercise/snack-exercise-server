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
import org.springframework.web.bind.annotation.*;


@Tag(name = "Member", description = "회원 API")
@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }

    @Operation(summary = "멤버 수정",
            description = "멤버 한명을 수정합니다.",
            security = { @SecurityRequirement(name = "bearer-key") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "멤버 수정 성공", content = @Content(schema = @Schema(implementation = MemberResponse.class)))
            })
    @Parameter(name = "memberId", description = "수정할 멤버 ID", required = true,  in = ParameterIn.PATH)
    @PutMapping("/members/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response update(@PathVariable Long id, @RequestBody MemberUpdateRequest request) {
        return Response.success(memberService.update(id, request));
    }
}
