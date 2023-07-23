package com.soma.snackexercise.dto.mission.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
public class TodayMissionCurrentResultDto {
    private List<MemberMissionDto> missionFlow;
    private Integer finishedRelayCount; // 현재까지 완료한 릴레이 횟수
    private LocalDate exgroupEndDate; // 그룹 종료 일자
}
