package com.soma.snackexercise.service.mission;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.dto.mission.response.MemberMissionDto;
import com.soma.snackexercise.dto.mission.response.RankingResponseDto;
import com.soma.snackexercise.dto.mission.response.TodayMissionCurrentResultDto;
import com.soma.snackexercise.exception.ExgroupNotFoundException;
import com.soma.snackexercise.repository.exgroup.ExgroupRepository;
import com.soma.snackexercise.repository.mission.MisssionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Object getTodayMissionRank(Long exgroupId) {
        Exgroup exgroup = exgroupRepository.findById(exgroupId).orElseThrow(ExgroupNotFoundException::new); // 존재하는 exgroupId인지 검증

        // 1. 오늘 날짜의 모든 미션 조회
        LocalDateTime now = LocalDateTime.now().minusDays(1);// 현재 날짜와 시간 가져오기
        LocalDateTime todayMidnight = now.with(LocalTime.MIN);// 오늘 자정 구하기
        LocalDateTime tomorrowMidnight = now.plusDays(1).with(LocalTime.MIN);// 내일 자정 구하기

        List<Mission> missions = misssionRepository.findAllExecutedMissionByGroupIdAndCreatedAt(exgroupId, todayMidnight, tomorrowMidnight);

        return getRankingList(missions);
    }

    private static List<RankingResponseDto> getRankingList(List<Mission> missions) {
        // 1. memberId 별로 평균 속도 계산
        Map<Long, RankingResponseDto> todayRankingMap = new HashMap<>();

        for (Mission mission : missions) {
            Long memberId = mission.getMember().getId();
            long minutesDiffBetweenCreateAndStart = ChronoUnit.MINUTES.between(mission.getCreatedAt(), mission.getStartAt());

            if(!todayRankingMap.containsKey(memberId)){
                Member member = mission.getMember();
                todayRankingMap.put(memberId, new RankingResponseDto(member.getName(), member.getProfileImage(), minutesDiffBetweenCreateAndStart, 1));
            }else{
                todayRankingMap.get(memberId).addTime(minutesDiffBetweenCreateAndStart);
            }
        }

        // 2. 평균 속도 계산
        todayRankingMap.forEach((k, v) -> v.calcAvgTime());

        // 3. 평균 속도 기준 오름차순 정렬
        List<RankingResponseDto> rankingList = todayRankingMap.values().stream()
                .sorted((a, b) -> (int) (a.getAvgMissionExecutionTime() - b.getAvgMissionExecutionTime())).toList();

        return rankingList;
    }
}
