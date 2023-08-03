package com.soma.snackexercise.domain.group;


import com.soma.snackexercise.domain.BaseEntity;
import com.soma.snackexercise.dto.group.request.GroupUpdateRequest;
import com.soma.snackexercise.exception.MaxMemberNumLessThanCurrentException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

import static java.lang.Boolean.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "Exgroup")
@Entity
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 그룹 이름

    private String emozi; // 그룹 대표 이모지

    private String color; // 그룹 대표 색상

    private String description; // 그룹 소개 글

    private Integer maxMemberNum; // 최대 참여 인원 수

    private Integer goalRelayNum; // 목표 릴레이 횟수

    private LocalTime startTime; // 시작 시간

    private LocalTime endTime; // 종료 시간

    private Integer existDays; // 그룹 목표 일수 // TODO 이름 가독성 괜찮은지

    private LocalDate startDate; // 시작 기간

    private LocalDate endDate; // 종료 기간

    private Boolean isGoalAchieved; // 성공 여부

    private String penalty; // 벌칙

    private String code; // 그룹 입장 코드

    private Integer checkIntervalTime; // 독촉 검사 시간 간격

    private Integer checkMaxNum; // 하루 독촉 검사 최대 횟수

    private Long currentDoingMemberId; // 현재 미션 수행 중인 회원의 ID

    public void updateMaxMemberNum(int currentMemberNum, int newMaxMemberNum) {
        if (currentMemberNum > newMaxMemberNum) {
            throw new MaxMemberNumLessThanCurrentException();
        }

        this.maxMemberNum = newMaxMemberNum;
    }

    public void updateCurrentDoingMemberId(Long memberId){
        this.currentDoingMemberId = memberId;
    }

    public void update(GroupUpdateRequest request) {
        this.name = request.getName();
        this.emozi = request.getEmozi();
        this.color = request.getColor();
        this.description = request.getDescription();
        this.goalRelayNum = request.getGoalRelayNum();
        this.startTime = request.getStartTime();
        this.endTime = request.getEndTime();
        this.penalty = request.getPenalty();
        this.checkIntervalTime = this.getCheckIntervalTime();
        this.checkMaxNum = this.getCheckMaxNum();
    }

    public void updateStartDateAndEndDate(){
        this.startDate = LocalDate.now();
        this.endDate = startDate.plusDays(existDays);
    }

    public void updateIsGoalAchieved(){
        this.isGoalAchieved = TRUE;
    }

    @Builder
    public Group(String name, String emozi, String color, String description,
                 Integer maxMemberNum, Integer goalRelayNum, LocalTime startTime,
                 LocalTime endTime, Integer existDays, String penalty, String code,
                 Integer checkIntervalTime, Integer checkMaxNum) {
        this.name = name;
        this.emozi = emozi;
        this.color = color;
        this.description = description;
        this.maxMemberNum = maxMemberNum;
        this.goalRelayNum = goalRelayNum;
        this.startTime = startTime;
        this.endTime = endTime;
        this.existDays = existDays;
        this.penalty = penalty;
        this.code = code;
        this.checkIntervalTime = checkIntervalTime;
        this.checkMaxNum = checkMaxNum;
        this.isGoalAchieved = FALSE;
        active();
    }

}
