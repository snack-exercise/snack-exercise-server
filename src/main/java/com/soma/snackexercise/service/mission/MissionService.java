package com.soma.snackexercise.service.mission;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.dto.mission.response.MemberMissionDto;
import com.soma.snackexercise.dto.mission.response.TodayMissionCurrentResultDto;
import com.soma.snackexercise.exception.ExgroupNotFoundException;
import com.soma.snackexercise.repository.exgroup.ExgroupRepository;
import com.soma.snackexercise.repository.mission.MisssionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MissionService {

    private final MisssionRepository misssionRepository;
    private final ExgroupRepository exgroupRepository;


    public TodayMissionCurrentResultDto getTodayMissionResults(Long exgroupId) {
        // 1. 그룹의 종료일자
        Exgroup exgroup = exgroupRepository.findById(exgroupId).orElseThrow(ExgroupNotFoundException::new);

        // 2. 그룹이 현재 완료한 릴레이 횟수
        LocalDateTime now = LocalDateTime.now();// 현재 날짜와 시간 가져오기
        LocalDateTime todayMidnight = now.with(LocalTime.MIN);// 오늘 자정 구하기
        LocalDateTime tomorrowMidnight = now.plusDays(1).with(LocalTime.MIN);// 내일 자정 구하기

        Integer currentFinishedRelayCount = misssionRepository.findCurrentFinishedRelayCountByGroupId(exgroupId, todayMidnight, tomorrowMidnight);

        // 3. 모든 그룹원의 오늘 수행한 미션 현황
        List<Mission> missions = misssionRepository.findAllMissionByGroupIdAndCreatedAt(exgroupId, todayMidnight, tomorrowMidnight);
        List<MemberMissionDto> missionFlow = missions.stream().map(mission -> new MemberMissionDto(mission.getMember().getId(), mission.getMember().getNickname(), mission.getMember().getProfileImage(), mission.getCreatedAt(), mission.getEndAt())).toList();

        return new TodayMissionCurrentResultDto(missionFlow, currentFinishedRelayCount, exgroup.getEndDate());
    }
}
