package com.soma.snackexercise.controller.notification;

import com.soma.snackexercise.dto.member.request.MemberUpdateFcmTokenRequest;
import com.soma.snackexercise.dto.mission.response.TodayMissionResultResponse;
import com.soma.snackexercise.service.notification.NotificationService;
import com.soma.snackexercise.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification", description = "알림 API")
@RequiredArgsConstructor
@RestController()
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "fcm 토큰 저장", description = "한 명의 회원에 대해 fcm 토큰을 저장합니다.",
            security = { @SecurityRequirement(name = "bearer-key") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "fcm 토큰 저장 성공", content = @Content(schema = @Schema(implementation = TodayMissionResultResponse.class))),
            })
    @PatchMapping("/members/notification")
    public Response updateFcmToken(@RequestBody MemberUpdateFcmTokenRequest request, @AuthenticationPrincipal UserDetails loginUser){
        return Response.success(notificationService.updateFcmToken(request, loginUser.getUsername()));
    }
}
