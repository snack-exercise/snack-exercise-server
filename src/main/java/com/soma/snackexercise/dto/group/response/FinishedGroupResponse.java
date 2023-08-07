package com.soma.snackexercise.dto.group.response;

import com.soma.snackexercise.domain.group.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FinishedGroupResponse {
    private Long groupId;
    private String groupName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer finishedRelayNum; // 그룹이 수행 완료한 횟수
    private Integer goalRelayNum; // 그룹 목표 릴레이 횟수
    private Boolean isGoalAchieved; // 그룹 릴레이 성공 여부

    public static FinishedGroupResponse toDto(Group group, Integer finishedRelayNum){
        return new FinishedGroupResponse(
                group.getId(),
                group.getName(),
                group.getStartDate(),
                group.getEndDate(),
                finishedRelayNum,
                group.getGoalRelayNum(),
                group.getIsGoalAchieved()
        );
    }
}
