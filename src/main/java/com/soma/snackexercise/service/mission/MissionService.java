package com.soma.snackexercise.service.mission;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.dto.exercise.response.ExerciseResponse;
import com.soma.snackexercise.dto.mission.request.MissionCancelRequest;
import com.soma.snackexercise.dto.mission.request.MissionFinishRequest;
import com.soma.snackexercise.dto.mission.request.MissionStartRequest;
import com.soma.snackexercise.dto.mission.response.*;
import com.soma.snackexercise.exception.group.GroupNotFoundException;
import com.soma.snackexercise.exception.group.InvalidGroupTimeException;
import com.soma.snackexercise.exception.group.NotStartedGroupException;
import com.soma.snackexercise.exception.joinlist.JoinListNotFoundException;
import com.soma.snackexercise.exception.member.MemberNotFoundException;
import com.soma.snackexercise.exception.mission.MissionNotFoundException;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.repository.mission.MissionRepository;
import com.soma.snackexercise.service.notification.FirebaseCloudMessageService;
import com.soma.snackexercise.util.constant.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.soma.snackexercise.domain.notification.NotificationMessage.ALLOCATE;
import static com.soma.snackexercise.domain.notification.NotificationMessage.GROUP_GOAL_ACHIEVE;
import static com.soma.snackexercise.util.constant.Status.ACTIVE;

/**
 * 미션 관련 서비스 클래스
 */

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final JoinListRepository joinListRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final MissionUtil missionUtil;

    /**
     * 오늘의 미션 결과를 조회합니다.
     * @param groupId 조회할 그룹의 ID
     * @return 오늘의 미션 결과
     */
    public TodayMissionResultResponse readTodayMissionResults(Long groupId) {
        // 1. 그룹의 종료일자
        Group group = groupRepository.findByIdAndStatus(groupId, ACTIVE).orElseThrow(GroupNotFoundException::new);

        // 2. 그룹이 현재 완료한 릴레이 횟수
        LocalDateTime now = LocalDateTime.now();// 현재 날짜와 시간 가져오기
        LocalDateTime todayMidnight = now.with(LocalTime.MIN);// 오늘 자정일 구하기
        LocalDateTime tomorrowMidnight = now.plusDays(1).with(LocalTime.MIN);// 내일 자정 구하기
        Integer currentFinishedRelayCount = missionRepository.findCurrentFinishedRelayCountByGroupId(groupId, todayMidnight, tomorrowMidnight);

        // 3. 모든 그룹원의 오늘 수행한 미션 현황
        List<Mission> missions = missionRepository.findFinishedMissionsByGroupIdWithinDateRange(groupId, todayMidnight, tomorrowMidnight);
        List<MemberMissionDto> missionFlow = missions.stream().map(MemberMissionDto::toDto).toList();

        return new TodayMissionResultResponse(missionFlow, currentFinishedRelayCount, group.getEndDate());
    }

    /**
     * 오늘의 미션 순위를 조회합니다.
     * @param exgroupId 조회할 그룹의 ID
     * @return 오늘의 미션 순위
     */
    public Object readTodayMissionRank(Long exgroupId) {
        if(!groupRepository.existsByIdAndStatus(exgroupId, ACTIVE)){
            throw new GroupNotFoundException();
        }

        // 1. 오늘 날짜의 모든 미션 조회
        LocalDateTime now = LocalDateTime.now();// 현재 날짜와 시간 가져오기
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
        Group group = groupRepository.findByIdAndStatus(exgroupId, ACTIVE).orElseThrow(GroupNotFoundException::new);
        if (group.getStartDate() == null){
            throw new NotStartedGroupException();
        }

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
        Mission mission = missionRepository.findFirstByGroupAndMemberOrderByCreatedAtDesc(group, member).orElseThrow(MissionNotFoundException::new);

        // 그룹이 현재 완료한 릴레이 횟수
        Integer currentFinishedRelayCount = joinListRepository.findMaxExecutedMissionCountByGroupAndStatus(group, Status.ACTIVE);

        // 현재 회차에서 몇 번째
        Integer currentRoundPosition = joinListRepository.findCurrentRoundPositionByGroupId(group, Status.ACTIVE, currentFinishedRelayCount) + 1;

        return MissionResponse.toDto(mission, currentFinishedRelayCount, currentRoundPosition);
    }

    @Transactional
    public MissionStartResponse missionStart(MissionStartRequest request) {
        Mission mission = missionRepository.findById(request.getMissionId()).orElseThrow(MissionNotFoundException::new);

        // 그룹의 시간 유효 시간 범위 내가 아니라면 nextMember null처리
        Group group = mission.getGroup();
        if(!group.isCurrentTimeBetweenStartTimeAndEndTime(LocalTime.now())){
            throw new InvalidGroupTimeException();
        }

        mission.startMission();
        log.info("[미션 시작 시각] {}", mission.getStartAt());
        return MissionStartResponse.toDto(mission);
    }

    /**
     * 미션을 중도 중단합니다.
     * @param request 미션 중단할 id
     */
    @Transactional
    public void cancelMission(MissionCancelRequest request) {
        Mission mission = missionRepository.findById(request.getMissionId()).orElseThrow(MissionNotFoundException::new);
        mission.cancelMission();
    }

    /**
     * 릴레이 미션 수행 완료를 기록하고, 다음 사람에게 미션을 할당합니다.
     * @param missionId 수행 완료할 미션 ID
     * @param request 해당 미션을 수행하여 얻은 소모칼로리, 수행한 운동영상의 길이
     * @return 그룹 목표 달성 여부
     */
    @Transactional // TODO 쿼리 성능 검증 필요
    public MissionFinishResponse finishMission(Long missionId, MissionFinishRequest request, String loginUserEmail) {
        // 1. 미션 테이블에 수행 완료 내용 기록
        Mission mission = missionRepository.findById(missionId).orElseThrow(MissionNotFoundException::new);
        mission.endMission(request.getCalory(), request.getLengthOfVideo());

        // 2. JoinList의 executedMissionCount 1 추가
        Member member = mission.getMember();
        Group group = mission.getGroup();
        JoinList joinList = joinListRepository.findByGroupAndMemberAndStatus(group, member, ACTIVE).orElseThrow(JoinListNotFoundException::new);
        joinList.addOneExecutedMissionCount();
        log.info("[미션 수행 완료] 그룹명 : {}, 회원명 : {}", group.getName(), member.getNickname());

        // 3. 모든 그룹원이 목표한 릴레이횟수만큼 수행 시, 그룹 목표 달성 및 푸시 알림 보내기
        if(joinListRepository.countGroupGoalAchievedMember(group) == joinListRepository.countGroupMember(group)){
            group.updateIsGoalAchieved(); // 그룹 목표 달성 여부 update

            // 멤버 전원에게 미션 성공 푸시 알림 전송
            List<String> tokenList = joinListRepository.findByGroupAndStatus(group, ACTIVE).stream().map(joinList1 -> joinList1.getMember().getFcmToken()).toList();
            firebaseCloudMessageService.sendByTokenList(tokenList, GROUP_GOAL_ACHIEVE.getTitle(), GROUP_GOAL_ACHIEVE.getBody());
            log.info("[그룹 목표 달성] 그룹명 : {}, 회원명 : {}", group.getName(), member.getNickname());

            return new MissionFinishResponse(group.getIsGoalAchieved());
        }

        // 4. 그룹의 시간 유효 시간 범위 내가 아니라면 다음 사람 null 처리 -> 그룹 시작 시간 스케줄러가 다음 사람 할당 수행
        if(!group.isCurrentTimeBetweenStartTimeAndEndTime(LocalTime.now())){
            log.info("그룹 운영시간이 아님");
            group.updateCurrentDoingMemberId(null);
            return new MissionFinishResponse(group.getIsGoalAchieved());
        }

        // 5. 다음 미션 할당자 선정 및 할당, 알림 전송
        Member nextMissionMember = missionUtil.getNextMissionMember(group);
        missionRepository.save(Mission.builder()
                .exercise(missionUtil.getRandomExercise())
                .member(nextMissionMember)
                .group(group)
                .build());
        group.updateCurrentDoingMemberId(nextMissionMember.getId());

        // 아직 fcm 프론트와 연동 전이라서 주석 처리
//        if(nextMissionMember.getFcmToken() == null){
//            throw new FcmTokenEmptyException();
//        }
        if(nextMissionMember.getFcmToken() != null){
            firebaseCloudMessageService.sendByToken(nextMissionMember.getFcmToken(), ALLOCATE.getTitle(), ALLOCATE.getBody());
        }
        log.info("[다음 미션] 그룹원 : {}, 할당 시각 : {}", nextMissionMember.getName(), LocalDateTime.now());
        return new MissionFinishResponse(group.getIsGoalAchieved());
    }

    /**
     * 비회원에게 랜덤 운동 1개를 할당합니다.
     * @return 랜덤 운동 1개
     */
    public ExerciseResponse getNonMemberRandomMission() {
        return ExerciseResponse.toDto(missionUtil.getRandomExercise());
    }

    /**
     * 회원에게 랜덤 운동 1개를 할당합니다.
     * @param email 이메일
     * @return 랜덤 운동 1개
     */
    @Transactional
    public ExerciseResponse getMemberRandomMission(String email) {
        Exercise randomExercise = missionUtil.getRandomExercise();
        Member member = memberRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(MemberNotFoundException::new);
        missionRepository.save(Mission.builder()
                .exercise(randomExercise)
                .member(member)
                .group(null)// TODO : FK null 가능은 하다만 무결성을 위해서는 피하는 게 좋다는 데...
                .build());

        return ExerciseResponse.toDto(randomExercise);
    }

    /**
     * 회원의 랜덤 운동 수행을 기록합니다.
     * @param missionId 수행 완료할 미션 ID
     * @param request 해당 미션을 수행하여 얻은 소모칼로리, 수행한 운동영상의 길이
     * @param email 이메일
     */
    @Transactional
    public void finishMemberRandomMission(Long missionId, MissionFinishRequest request, String email) {
        Mission mission = missionRepository.findById(missionId).orElseThrow(MissionNotFoundException::new);
        mission.endMission(request.getCalory(), request.getLengthOfVideo());
    }
}
