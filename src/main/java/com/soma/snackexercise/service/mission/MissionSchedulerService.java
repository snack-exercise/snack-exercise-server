package com.soma.snackexercise.service.mission;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.exception.MemberNotFoundException;
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
import static java.lang.Math.abs;

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

    private static Random random = new Random();

    /**
     * 5초 마다 현재 시각과 그룹의 시작시각 차이가 5초 이하라면, 그룹원 한 명에게 미션을 할당하고 알림을 보냅니다.
     */
    @Scheduled(fixedRate = 5000) // 이전 Task 시작 시점으로부터 5초가 지난 후 Task 실행
    public void allocateMissionAtGroupStartTime(){
        List<Group> groupList = groupRepository.findAllByStatus(Status.ACTIVE);
        List<String> tokenList = new ArrayList<>();

        // 모든 그룹에 대해서 현재 시각이 그룹의 시작시간과 시간차이가 5초 이하인 그룹에 대해서 미션 할당 및 알림 보내기
        LocalTime now = LocalTime.now();
        for (Group group : groupList) {
            long timeDiff = abs(ChronoUnit.SECONDS.between(now, group.getStartTime()));

            // 그룹 시작시각과 현재시각의 차이가 5이상이라면, 알림을 보내지 않음
            if(timeDiff >= 5){
                return;
            }
            Member targetMember = getMissionAllocatedMember(group);
            tokenList.add(targetMember.getFcmToken());

            missionRepository.save(Mission.builder()
                    .exercise(getRandomExercise())
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

    /**
     * 특정 그룹에서 운동 미션을 할당할 회원을 선정하여 반환합니다.
     * @param group 그룹
     * @return 운동 미션이 할당된 회원
     */
    Member getMissionAllocatedMember(Group group){
        Member targetMember;
        if(group.getCurrentDoingMemberId() != null){// 1. 현재 수행중인 멤버가 있다면, 해당 멤버에게 미션 알림 보내기
            targetMember = memberRepository.findByIdAndStatus(group.getCurrentDoingMemberId(), Status.ACTIVE).orElseThrow(MemberNotFoundException::new);
        }else{// 2. 현재 수행중인 멤버가 없다면(처음 할당), 그룹 내 미션 수행 횟수가 가장 적은 사람 중에서 랜덤한 멤버에게 미션 할당 및 알림 보내기
            List<Member> minExecutedMemberList = joinListRepository.findMinExecutedMemberList(group);
            int randomIndex = random.nextInt(minExecutedMemberList.size());
            targetMember = minExecutedMemberList.get(randomIndex);
        }
        return targetMember;
    }

    /**
     *
     * @return 랜덤한 운동 1개를 반환합니다.
     */
    Exercise getRandomExercise(){
        List<Exercise> exerciseList = exerciseRepository.findAll();
        int randomIndex = random.nextInt(exerciseList.size());
        return exerciseList.get(randomIndex);
    }
}
