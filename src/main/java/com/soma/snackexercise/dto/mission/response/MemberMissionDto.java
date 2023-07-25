package com.soma.snackexercise.dto.mission.response;

import com.soma.snackexercise.domain.mission.Mission;
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

    public static MemberMissionDto toDto(Mission mission) {
        return new MemberMissionDto(
                mission.getMember().getId(),
                mission.getMember().getNickname(),
                mission.getMember().getProfileImage(),
                mission.getStartAt(),
                mission.getEndAt()
        );
    }
}
