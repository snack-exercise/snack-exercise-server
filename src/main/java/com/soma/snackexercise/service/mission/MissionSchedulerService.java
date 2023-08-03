package com.soma.snackexercise.service.mission;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.repository.exercise.ExerciseRepository;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.repository.mission.MissionRepository;
import com.soma.snackexercise.service.notification.FirebaseCloudMessageService;
import com.soma.snackexercise.util.constant.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.soma.snackexercise.domain.notification.NotificationMessage.ALLOCATE;
import static com.soma.snackexercise.domain.notification.NotificationMessage.REMINDER;

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
    @Scheduled(fixedRate = 5000) // 이전 Task 시작 시점으로부터 5초가 지난 후 Task 실행
    public void allocateMissionAtGroupStartTime(){
        // todo : 종료 여부가 false인 group에 대해서
        List<Group> groupList = groupRepository.findAllByStatus(Status.ACTIVE);
        List<String> tokenList = new ArrayList<>();
        List<Exercise> exerciseList = exerciseRepository.findAll();

        // 모든 그룹에 대해서 현재 시각이 그룹의 시작시간과 시간차이가 5초 이하인 그룹에 대해서 미션 할당 및 알림 보내기
        LocalTime now = LocalTime.now();
        for (Group group : groupList) {
            long timeDiff = ChronoUnit.SECONDS.between(now, group.getStartTime());

            // 그룹 시작시각과 현재시각의 차이가 5이상이거나 음수라면 알림을 보내지 않음
            if(timeDiff >= 5 || timeDiff < 0){
                continue;
            }
            Member targetMember = missionUtil.getMissionAllocatedMember(group);
            tokenList.add(targetMember.getFcmToken());

            missionRepository.save(Mission.builder()
                    .exercise(exerciseList.get(random.nextInt(exerciseList.size())))
                    .member(targetMember)
                    .group(group)
                    .build());

            log.info("그룹명 : {}, 그룹원 : {}, 할당 시각 : {}", group.getName(), targetMember.getName(), LocalDateTime.now());
        }

        if(!tokenList.isEmpty()){
            // tokenList로 알림 보내기
            firebaseCloudMessageService.sendByTokenList(tokenList, ALLOCATE.getTitle(), ALLOCATE.getBody());
        }
    }

    @Scheduled(fixedRate = 5000)
    public void sendReminderNotifications() {
        // todo : 종료 여부가 false인 그룹에 대해서
        List<Group> groupList = groupRepository.findAllByStatus(Status.ACTIVE);

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
                    mission.getAlarmCount() >= group.getCheckMaxNum()||
                    !mission.isValidTimeReminder(localDateTimeNow, group.getCheckIntervalTime(), 5)) {
                continue;
            }

            List<JoinList> joinLists = joinListRepository.findByGroupAndStatus(group, Status.ACTIVE);

            joinLists.forEach(joinList -> tokenList.add(joinList.getMember().getFcmToken()));

            firebaseCloudMessageService.sendByTokenList(tokenList, REMINDER.getTitleWithNickname(targetMember.getNickname()), REMINDER.getBodyWithNickname(targetMember.getNickname()));
        }
    }

    @Scheduled(fixedRate = 5000)
    public void sendReminderNotifications() {

    }
}
