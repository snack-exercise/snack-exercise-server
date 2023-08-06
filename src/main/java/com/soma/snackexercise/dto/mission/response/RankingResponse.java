package com.soma.snackexercise.dto.mission.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingResponse {
    private String nickname;
    private String profileImage;
    private long avgMissionExecutionTime; // 미션 평균 반응 속도
    private int missionCount; // 수행한 미션 개수

    public void addMission(long missionExecutionTime) {
        this.avgMissionExecutionTime += missionExecutionTime;
        this.missionCount += 1;
    }

    public void calcAvgTime(){
        this.avgMissionExecutionTime /= this.missionCount;
    }
}
