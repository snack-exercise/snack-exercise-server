package com.soma.snackexercise.dto.mission.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingResponseDto {
    private String nickname;
    private String profileImage;
    private long avgMissionExecutionTime; // 미션 평균 반응 속도
    private int missionCount; // 수행한 미션 개수

    public void addTime(long missionExecutionTime){
        this.avgMissionExecutionTime += missionExecutionTime;
    }

    public long calcAvgTime(){
        this.avgMissionExecutionTime /= this.missionCount;
        return this.avgMissionExecutionTime;
    }

    public int addMissionCount(){
        this.missionCount += 1;
        return this.missionCount;
    }
}
