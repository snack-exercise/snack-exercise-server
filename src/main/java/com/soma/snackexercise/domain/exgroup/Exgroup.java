package com.soma.snackexercise.domain.exgroup;


import com.soma.snackexercise.domain.BaseEntity;
import com.soma.snackexercise.dto.exgroup.request.ExgroupUpdateRequest;
import com.soma.snackexercise.exception.MaxMemberNumLessThanCurrentException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Exgroup extends BaseEntity {
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

    private String penalty; // 벌칙

    private String code; // 그룹 입장 코드

    private Integer missionIntervalTime; // 미션 수행 시간 간격

    private Integer checkIntervalTime; // 미션 수행 체크 시간 간격

    private Integer checkMaxNum; // 일별 미션 수행 체크 최대 횟수

    private Long currentDoingMemberId; // 현재 미션 수행 중인 회원의 ID

    public void updateMaxMemberNum(int currentMemberNum, int newMaxMemberNum) {
        if (currentMemberNum > newMaxMemberNum) {
            throw new MaxMemberNumLessThanCurrentException();
        }

        this.maxMemberNum = newMaxMemberNum;
    }

    public void update(ExgroupUpdateRequest request) {
        this.name = request.getName();
        this.emozi = request.getEmozi();
        this.color = request.getColor();
        this.description = request.getDescription();
        this.goalRelayNum = request.getGoalRelayNum();
        this.startTime = request.getStartTime();
        this.endTime = request.getEndTime();
        this.penalty = request.getPenalty();
        this.missionIntervalTime = this.getMissionIntervalTime();
        this.checkIntervalTime = this.getCheckIntervalTime();
        this.checkMaxNum = this.getCheckMaxNum();
    }

    @Builder
    public Exgroup(String name, String emozi, String color, String description,
                   Integer maxMemberNum, Integer goalRelayNum, LocalTime startTime,
                   LocalTime endTime, String penalty, String code, Integer missionIntervalTime,
                   Integer checkIntervalTime, Integer checkMaxNum) {
        this.name = name;
        this.emozi = emozi;
        this.color = color;
        this.description = description;
        this.maxMemberNum = maxMemberNum;
        this.goalRelayNum = goalRelayNum;
        this.startTime = startTime;
        this.endTime = endTime;
        this.penalty = penalty;
        this.code = code;
        this.missionIntervalTime = missionIntervalTime;
        this.checkIntervalTime = checkIntervalTime;
        this.checkMaxNum = checkMaxNum;
        active();
    }

}
