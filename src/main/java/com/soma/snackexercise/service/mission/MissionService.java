package com.soma.snackexercise.service.mission;

import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.dto.mission.response.MemberMissionDto;
import com.soma.snackexercise.dto.mission.response.MissionResponse;
import com.soma.snackexercise.dto.mission.response.RankingResponse;
import com.soma.snackexercise.dto.mission.response.TodayMissionResultResponse;
import com.soma.snackexercise.exception.GroupNotFoundException;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.exception.MissionNotFoundException;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.repository.mission.MissionRepository;
import com.soma.snackexercise.util.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 미션 관련 서비스 클래스
 */

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final JoinListRepository joinListRepository;

    /**
     * 오늘의 미션 결과를 조회합니다.
     * @param groupId 조회할 그룹의 ID
     * @return 오늘의 미션 결과
     */
    public TodayMissionResultResponse readTodayMissionResults(Long groupId) {
        // 1. 그룹의 종료일자
        Group group = groupRepository.findByIdAndStatus(groupId, Status.ACTIVE).orElseThrow(GroupNotFoundException::new);

        // 2. 그룹이 현재 완료한 릴레이 횟수
        LocalDateTime now = LocalDateTime.now();// 현재 날짜와 시간 가져오기
        LocalDateTime todayMidnight = now.with(LocalTime.MIN);// 오늘 자정일 구하기
        LocalDateTime tomorrowMidnight = now.plusDays(1).with(LocalTime.MIN);// 내일 자정 구하기
        Integer currentFinishedRelayCount = missionRepository.findCurrentFinishedRelayCountByGroupId(groupId, todayMidnight, tomorrowMidnight);

        // 3. 모든 그룹원의 오늘 수행한 미션 현황
        List<Mission> missions = missionRepository.findMissionsByGroupIdWithinDateRange(groupId, todayMidnight, tomorrowMidnight);
        List<MemberMissionDto> missionFlow = missions.stream().map(MemberMissionDto::toDto).toList();

        return new TodayMissionResultResponse(missionFlow, currentFinishedRelayCount, group.getEndDate());
    }

    /**
     * 오늘의 미션 순위를 조회합니다.
     * @param exgroupId 조회할 그룹의 ID
     * @return 오늘의 미션 순위
     */
    public Object readTodayMissionRank(Long exgroupId) {
        if(!groupRepository.existsByIdAndStatus(exgroupId, Status.ACTIVE)){
            throw new GroupNotFoundException();
        }

        // 1. 오늘 날짜의 모든 미션 조회
        LocalDateTime now = LocalDateTime.now().minusDays(1);// 현재 날짜와 시간 가져오기
        LocalDateTime todayMidnight = now.with(LocalTime.MIN);// 오늘 자정 구하기
        LocalDateTime tomorrowMidnight = now.plusDays(1).with(LocalTime.MIN);// 내일 자정 구하기

        List<Mission> missions = missionRepository.findFinishedMissionsByGroupIdWithinDateRange(exgroupId, todayMidnight, tomorrowMidnight);

        return calcRankFromMissionList(missions);
    }

    /**
     * 누적 미션 순위를 조회합니다.
     * @param exgroupId 조회할 그룹의 ID
     * @return 누적 미션 순위
     */
    public Object readCumulativeMissionRank(Long exgroupId) {
        Group group = groupRepository.findByIdAndStatus(exgroupId, Status.ACTIVE).orElseThrow(GroupNotFoundException::new);

        List<Mission> missions = missionRepository.findFinishedMissionsByGroupIdWithinDateRange(exgroupId, group.getStartDate().atStartOfDay(), group.getEndDate().atStartOfDay().plusDays(1));

        return calcRankFromMissionList(missions);
    }

    /**
     * 미션 리스트로부터 순위를 계산합니다.
     * @param missions 순위 계산에 사용될 미션 리스트
     * @return 순위 리스트
     */
    private static List<RankingResponse> calcRankFromMissionList(List<Mission> missions) {
        // 1. memberId 별로 평균 속도 계산
        Map<Long, RankingResponse> todayRankingMap = new HashMap<>();

        for (Mission mission : missions) {
            Long memberId = mission.getMember().getId();
            long minutesDiffBetweenCreateAndStart = mission.calculateMinutesDiffBetweenCreateAndStart();

            if(!todayRankingMap.containsKey(memberId)){
                Member member = mission.getMember();
                todayRankingMap.put(memberId, new RankingResponse(member.getNickname(), member.getProfileImage(), minutesDiffBetweenCreateAndStart, 1));
            }else{
                todayRankingMap.get(memberId).addMission(minutesDiffBetweenCreateAndStart);
            }
        }

        // 2. 평균 속도 계산
        todayRankingMap.forEach((k, v) -> v.calcAvgTime());

        // 3. 평균 속도 기준 오름차순 정렬
        return todayRankingMap.values().stream()
                .sorted((a, b) -> (int) (a.getAvgMissionExecutionTime() - b.getAvgMissionExecutionTime())).toList();
    }

    public MissionResponse read(Long groupId, String email) {
        Group group = groupRepository.findByIdAndStatus(groupId, Status.ACTIVE).orElseThrow(GroupNotFoundException::new);
        Member member = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);
        Mission mission = missionRepository.findFirstByGroupAndMemberAndEndAtIsNotNullOrderByCreatedAtDesc(group, member).orElseThrow(MissionNotFoundException::new);

        // 그룹이 현재 완료한 릴레이 횟수
        Integer currentFinishedRelayCount = joinListRepository.findMaxExecutedMissionCountByGroupAndStatus(group, Status.ACTIVE);

        // 현재 회차에서 몇 번째
        Integer currentRoundPosition = joinListRepository.findCurrentRoundPositionByGroupId(group, Status.ACTIVE) + 1;

        return MissionResponse.toDto(mission, currentFinishedRelayCount, currentRoundPosition);
    }
}
