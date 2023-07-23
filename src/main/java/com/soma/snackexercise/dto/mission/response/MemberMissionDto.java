package com.soma.snackexercise.dto.mission.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberMissionDto {
    private Long memberId;
    private String memberName;
    private String profileImage;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
