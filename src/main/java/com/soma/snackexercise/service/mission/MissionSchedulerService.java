package com.soma.snackexercise.service.mission;

import com.soma.snackexercise.domain.BaseEntity;
import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.exception.member.FcmTokenEmptyException;
import com.soma.snackexercise.exception.mission.MissionNotFoundException;
import com.soma.snackexercise.repository.exercise.ExerciseRepository;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.repository.mission.MissionRepository;
import com.soma.snackexercise.service.notification.FirebaseCloudMessageService;
import com.soma.snackexercise.util.constant.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.soma.snackexercise.domain.notification.NotificationMessage.*;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class MissionSchedulerService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final JoinListRepository joinListRepository;
    private final ExerciseRepository exerciseRepository;
    private final MissionRepository missionRepository;
    private final MissionUtil missionUtil;

    private static Random random = new Random();

    /**
     * 5초 마다 현재 시각과 그룹의 시작시각 차이가 5초 이하라면, 그룹원 한 명에게 미션을 할당하고 알림을 보냅니다.
     */
    // todo : Transactionl을 붙여주는게 나을까..?
    //@Scheduled(fixedRate = 5000) // 이전 Task 시작 시점으로부터 5초가 지난 후 Task 실행
    //@Async
    @Scheduled(cron = "1 * * * * *") // 1분마다 실행, cron 표현식
    @Transactional
    public void allocateMissionAtGroupStartTime() {
        log.info("============= 그룹 시작 시간 미션 할당 스케줄러 =============");
        // 종료 여부가 false이고, 시작한 그룹에 대해서
        List<Group> groupList = groupRepository.findAllByStartDateNotNullAndIsGoalAchievedAndStatus(false, Status.ACTIVE);
        List<Exercise> exerciseList = exerciseRepository.findAll();

        // 모든 그룹에 대해서 현재 시각이 그룹의 시작시간과 시간차이가 5초 이하인 그룹에 대해서 미션 할당 및 알림 보내기
        System.out.println("not test : " + groupList.size());
        LocalTime now = LocalTime.now();
        for (Group group : groupList) {
            allocateMissionForGroup(now, group, exerciseList);
        }
    }

    /**
     * 미션 할당 이후, 지정한 시간만큼 시간이 흐른 뒤에 운동 수행 여부를 확인합니다. 수행을 하지 않았을 경우, 미션 수행 차례인 사람에게는 수행 장려메세지가, 그룹 전원에게 독촉을 장려하는 메세지가 전달됩니다.
     */
    //@Scheduled(fixedRate = 5000)
    @Async
    @Transactional
    @Scheduled(cron = "0 * * * * *") // 1분마다 실행, cron 표현식
    public void sendReminderNotifications() {
        log.info("============= 자동 독촉 알람 스케줄러 =============");
        // 종료 여부가 false인 그룹에 대해서
        List<Group> groupList = groupRepository.findAllByIsGoalAchievedAndStatus(false, Status.ACTIVE);
        LocalTime localTimeNow = LocalTime.now();
        LocalDateTime localDateTimeNow = LocalDateTime.now();

        for (Group group : groupList) {
            List<String> tokenList = new ArrayList<>();
            Long targetMemberId = group.getCurrentDoingMemberId();
            Member targetMember = memberRepository.findByIdAndStatus(targetMemberId, Status.ACTIVE).orElse(null);
            Mission mission = missionRepository.findFirstByGroupAndMemberOrderByCreatedAtDesc(group, targetMember).orElse(null);
            /*
             만약 현재 시간이 그룹 시작 시간과 끝 시간 사이가 아니거나
             현재 진행중인 멤버가 없거나
             그룹과 진행중인 멤버 관련 미션이 없거나
             미션이 이미 시작되었거나
             미션 독촉 횟수가 그룹 최대 검사 횟수보다 크거나 같거나
             미션의 할당 일시와 현재 시각차가 checkIntervalTime의 차이가 5 이상이라면
             continue
             */
            if (!group.isCurrentTimeBetweenStartTimeAndEndTime(localTimeNow) ||
                    targetMemberId == null ||
                    mission == null ||
                    mission.getStartAt() != null ||
                    mission.getAlarmCount() >= group.getCheckMaxNum() ||
                    !mission.isValidTimeReminder(localDateTimeNow, group.getCheckIntervalTime(), 1)) {
                log.info("[그룹명] : {}, 독촉 대상이 없습니다.", group.getName());
                continue;
            }
            log.info("[그룹명] : {}, [미션 ID] : {}, [미션 수행자명] : {}", group.getName(), mission.getId(), targetMember.getNickname());
            mission.addOneAlarmCount();

            // 푸시 알림 전송
            // 미션 수행자 푸시 알림 전송
            String targetMemberFcmToken = targetMember.getFcmToken();
            if (targetMemberFcmToken != null && !targetMemberFcmToken.isEmpty()){
                firebaseCloudMessageService.sendByToken(targetMemberFcmToken, AUTOMATIC_REMINDER_TARGETMEMBER.getTitleWithGroupName(group.getName()), AUTOMATIC_REMINDER_TARGETMEMBER.getBody());
            }

            // 미션 수행자 제외한 그룹원에게 푸시 알림 전송
            List<JoinList> joinLists = joinListRepository.findByGroupAndStatus(group, Status.ACTIVE);
            for (JoinList joinList : joinLists) {
                if(joinList.getMember() != targetMember && joinList.getMember().getFcmToken() != null){
                    tokenList.add(joinList.getMember().getFcmToken());
                }
            }
            //joinLists.forEach(joinList -> tokenList.add(joinList.getMember().getFcmToken()));
            if(!tokenList.isEmpty()){
                firebaseCloudMessageService.sendByTokenList(tokenList, AUTOMATIC_REMINDER_FOR_GROUPMEMBER.getTitleWithGroupNameAndNickname(group.getName(), targetMember.getNickname()), AUTOMATIC_REMINDER_FOR_GROUPMEMBER.getBodyWithNickname(targetMember.getNickname()));
            }

        }
    }

    /**
     * 1시간 마다 스케줄러가 그룹의 종료일자를 기반으로 그룹을 종료합니다.
     */
    @Async
    @Transactional
    //@Scheduled(fixedRate = 5000
    @Scheduled(cron = "0 0 0/1 * * *") // 1시간마다 실행, cron 표현식
    public void inActiveGroupsPastEndDate() {
        log.info("============= 그룹 종료 일자 스케줄러 =============");
        // 종료 여부에 관계없이 group이 Active한 그룹들 중에서 EndDate가 지난 그룹
        LocalDate now = LocalDate.now();
        List<Group> groupList = groupRepository.findAllByEndDateLessThanAndStatus(now, Status.ACTIVE);

        for (Group group : groupList) {
            // 그룹 inActive
            group.inActive();

            List<JoinList> joinLists = joinListRepository.findByGroupAndStatus(group, Status.ACTIVE);

            // JoinList들에게 알람 전송
            sendNotificationsToJoinList(group, joinLists);

            // JoinList 모두 비활성화
            joinLists.forEach(BaseEntity::inActive);
        }

    }

    @Transactional
    private void allocateMissionForGroup(LocalTime now, Group group, List<Exercise> exerciseList) {
        long timeDiff = ChronoUnit.MINUTES.between(now, group.getStartTime()); // group.getStartTime - now

        // 현재 시간이 시작 시간보다 이전이라면 continue
        if(now.isBefore(group.getStartTime())){
            return;
        }
        log.info("[시각 차이] : {}분, [그룹명] : {}", timeDiff, group.getName());
        // 그룹 시작시각과 현재시각의 차이가 5이상이거나 음수라면 알림을 보내지 않음
        if (timeDiff >= 1 || timeDiff < 0) {
            return;
        }
        Member targetMember = missionUtil.getMissionAllocatedMember(group);
        if (targetMember.getFcmToken() == null) {
            throw new FcmTokenEmptyException();
        }

        if (group.getCurrentDoingMemberId() != null) {
            Mission mission = missionRepository.findFirstByGroupAndMemberOrderByCreatedAtDesc(group, targetMember).orElseThrow(MissionNotFoundException::new);
            mission.updateCreatedAt(LocalDateTime.now());
            log.info("시작시간 스케줄러 (미션 기존 할당 시간 변경) : 그룹명 : {}, 그룹원 : {}, 할당 시각 : {}", group.getName(), targetMember.getNickname(), LocalDateTime.now());
        }
        else{
            missionRepository.save(Mission.builder()
                    .exercise(exerciseList.get(random.nextInt(exerciseList.size())))
                    .member(targetMember)
                    .group(group)
                    .build());
            log.info("시작시간 스케줄러 (미션 생성) : 그룹명 : {}, 그룹원 : {}, 할당 시각 : {}", group.getName(), targetMember.getNickname(), LocalDateTime.now());
        }

        log.info("그룹명 : {}, 그룹원 : {}, 할당 시각 : {}", group.getName(), targetMember.getNickname(), LocalDateTime.now());

        if (!targetMember.getFcmToken().isEmpty()) {
            firebaseCloudMessageService.sendByToken(targetMember.getFcmToken(), ALLOCATE.getTitleWithGroupName(group.getName()), ALLOCATE.getBody());
        }
    }

    private void sendNotificationsToJoinList(Group group, List<JoinList> joinLists) {
        List<String> tokenList = new ArrayList<>();
        for (JoinList joinList : joinLists) {
            if (joinList.getMember().getFcmToken() != null) {
                tokenList.add(joinList.getMember().getFcmToken());
            }
        }
        //joinLists.forEach(joinList -> tokenList.add(joinList.getMember().getFcmToken()));
        if (!tokenList.isEmpty()) {
            firebaseCloudMessageService.sendByTokenList(tokenList, GROUP_END.getTitleWithGroupName(group.getName()), GROUP_END.getBody());

        }
    }
}