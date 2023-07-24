package com.soma.snackexercise.controller.mission;

import com.soma.snackexercise.service.mission.MissionService;
import com.soma.snackexercise.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mission", description = "미션 API")
@RequiredArgsConstructor
@RestController()
public class MissionController {
    private final MissionService missionService;

    @Operation(summary = "당일 미션 수행 현황 조회", description = "당일 미션 수행 현황을 조회합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @Parameter(name = "exgroupId", description = "조회할 운동 그룹 ID")
    @GetMapping("/exgroups/{exgroupId}/missions")
    public Response getTodayMissionResults(@PathVariable("exgroupId") Long exgroupId){
        return Response.success(missionService.getTodayMissionResults(exgroupId));
    }
}