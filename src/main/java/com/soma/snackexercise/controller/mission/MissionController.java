package com.soma.snackexercise.controller.mission;

import com.soma.snackexercise.dto.mission.request.MissionCancelRequest;
import com.soma.snackexercise.dto.mission.request.MissionFinishRequest;
import com.soma.snackexercise.dto.mission.request.MissionStartRequest;
import com.soma.snackexercise.dto.mission.response.RankingResponse;
import com.soma.snackexercise.dto.mission.response.TodayMissionResultResponse;
import com.soma.snackexercise.exception.WrongRequestParamException;
import com.soma.snackexercise.service.mission.MissionService;
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

@Tag(name = "Mission", description = "미션 API")
@RequiredArgsConstructor
public class MissionController {
    private final MissionService missionService;

    @Operation(summary = "당일 미션 수행 현황 조회", description = "당일 미션 수행 현황을 조회합니다.",
            security = { @SecurityRequirement(name = "bearer-key") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "당일 미션 수행 현황 조회 성공", content = @Content(schema = @Schema(implementation = TodayMissionResultResponse.class))),
                    @ApiResponse(responseCode = "404", description = "당일 미션 수행 현황을 찾을 수 없음", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @Parameter(name = "groupId", description = "조회할 운동 그룹 ID", required = true,  in = ParameterIn.PATH)
    @GetMapping("/groups/{groupId}/missions")
    @ResponseStatus(HttpStatus.OK)
    public Response getTodayMissionResults(@PathVariable("groupId") Long groupId){
        return Response.success(missionService.readTodayMissionResults(groupId));
    }


    @Operation(summary = "당일 미션 랭킹 조회", description = "미션 랭킹을 조회합니다.",
            security = { @SecurityRequirement(name = "bearer-key") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "당일 미션 랭킹 조회 성공", content = @Content(schema = @Schema(implementation = RankingResponse.class))),
                    @ApiResponse(responseCode = "404", description = "당일 미션 랭킹을 찾을 수 없음", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @Parameter(name = "groupId", description = "조회할 운동 그룹 ID", required = true,  in = ParameterIn.PATH)
    @Parameter(name = "filter", description = "당일 랭킹 : today, 누적 랭킹 : total", required = true,  in = ParameterIn.QUERY)
    @GetMapping("/groups/{groupId}/missions/rank")
    @ResponseStatus(HttpStatus.OK)
    public Response getMissionRank(@PathVariable("groupId") Long groupId, @RequestParam String filter){
        if(filter.equals("today")){
            return Response.success(missionService.readTodayMissionRank(groupId));
        }else if(filter.equals("total")){
            return Response.success(missionService.readCumulativeMissionRank(groupId));
        }
        throw new WrongRequestParamException(); // TODO query param 검증을 이런 식으로 해도 될지
    }
    @Operation(summary = "미션 정보 읽기", description = "미션 정보를 읽습니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @Parameter(name = "groupId", description = "사용자가 속한 그룹 id")
    @GetMapping("/missions/groups/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public Response read(@PathVariable Long groupId, @AuthenticationPrincipal UserDetails loginUser) {
        return Response.success(missionService.read(groupId, loginUser.getUsername()));
    }

    @Operation(summary = "미션 시작하기", description = "미션을 시작합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping("/missions/start")
    @ResponseStatus(HttpStatus.OK)
    public Response startMission(@RequestBody MissionStartRequest request) {
        return Response.success(missionService.missionStart(request));
    }

    @Operation(summary = "미션 중도 중단", description = "미션을 중도에 중단합니다.", security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping("/missions/cancel")
    @ResponseStatus(HttpStatus.OK)
    public Response cancelMission(@RequestBody MissionCancelRequest request) {
        missionService.cancelMission(request);
        return Response.success();
    }

    @Operation(summary = "미션 수행 완료", description = "할당된 미션을 수행 완료합니다.",
            security = { @SecurityRequirement(name = "bearer-key") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "미션 수행 완료 성공", content = @Content(schema = @Schema(implementation = RankingResponse.class))),
                    @ApiResponse(responseCode = "404", description = "미션을 찾을 수 없음", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @Parameter(name = "missionId", description = "수행 완료할 미션 ID", required = true,  in = ParameterIn.PATH)
    @PostMapping("missions/{missionId}/finish")
    @ResponseStatus(HttpStatus.OK)
    public Response finishMission(@PathVariable("missionId") Long missionId, @RequestBody MissionFinishRequest request, @AuthenticationPrincipal UserDetails loginUser){
        return Response.success(missionService.finishMission(missionId, request, loginUser.getUsername()));
    }
}
