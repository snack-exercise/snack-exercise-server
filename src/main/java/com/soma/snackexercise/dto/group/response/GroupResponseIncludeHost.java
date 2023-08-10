package com.soma.snackexercise.dto.group.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.soma.snackexercise.domain.group.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponseIncludeHost {
    private Long groupId;

    private String groupName; // 그룹 이름

    private String emozi; // 그룹 대표 이모지

    private String color; // 그룹 대표 색상

    private String description; // 그룹 소개 글

    private Integer maxMemberNum; // 최대 참여 인원 수

    private Integer goalRelayNum; // 목표 릴레이 횟수

    @JsonSerialize(using = LocalTimeSerializer.class) // 객체 -> JSON
    private LocalTime startTime; // 시작 시간

    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime endTime; // 종료 시간

    private Integer existDays; // 그룹 목표 일수

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate; // 시작 기간

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate endDate; // 종료 기간

    private String code;

    private String penalty; // 벌칙

    private Integer checkIntervalTime; // 미션 수행 체크 시간 간격

    private Integer checkMaxNum; // 일별 미션 수행 체크 최대 횟수

    private Long hostMemberId; // 방장 ID

    public static GroupResponseIncludeHost toDto(Group group, Long hostMemberId) {
        return new GroupResponseIncludeHost(
                group.getId(),
                group.getName(),
                group.getEmozi(),
                group.getColor(),
                group.getDescription(),
                group.getMaxMemberNum(),
                group.getGoalRelayNum(),
                group.getStartTime(),
                group.getEndTime(),
                group.getExistDays(),
                group.getStartDate(),
                group.getEndDate(),
                group.getCode(),
                group.getPenalty(),
                group.getCheckIntervalTime(),
                group.getCheckMaxNum(),
                hostMemberId
        );
    }
}
