package com.soma.snackexercise.dto.exgroup.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetOneExgroupResponse {

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
}
