package com.soma.snackexercise.controller.mission;

import com.soma.snackexercise.dto.mission.request.MissionMvpStartRequest;
import com.soma.snackexercise.service.mission.MissionMvpService;
import com.soma.snackexercise.util.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MissionMvpController {
    private final MissionMvpService missionMvpService;

    @PostMapping("/mvp/missions/start")
    private Response missionStart(@RequestBody MissionMvpStartRequest request) {
        return Response.success(missionMvpService.missionStart(request));
    }
}
