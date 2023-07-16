package com.soma.snackexercise.domain.exgroup;


import com.soma.snackexercise.util.constant.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Exgroup {
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

    private LocalDate startDate; // 시작 날짜

    private LocalDate endDate; // 종료 날짜

    private Long currentDoingMemberId; // 현재 미션 수행 중인 회원의 ID

    private Status status; // 상태 (INACTIVE = 삭제)


    @Builder
    public Exgroup(String name, String emozi, String color, String description,
                   Integer maxMemberNum, Integer goalRelayNum, LocalTime startTime,
                   LocalTime endTime, String penalty, String code, Integer missionIntervalTime,
                   Integer checkIntervalTime, Integer checkMaxNum, LocalDate startDate,
                   LocalDate endDate) {
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
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = Status.ACTIVE;
    }

    public void inActive(){
        this.status = Status.INACTIVE;
    }


}
