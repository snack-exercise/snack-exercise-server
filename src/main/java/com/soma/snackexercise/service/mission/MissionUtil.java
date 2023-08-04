package com.soma.snackexercise.service.mission;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.repository.exercise.ExerciseRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.repository.mission.MissionRepository;
import com.soma.snackexercise.util.constant.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Component // TODO @Service? @Component?
public class MissionUtil {
    private final MemberRepository memberRepository;
    private final JoinListRepository joinListRepository;
    private final ExerciseRepository exerciseRepository;
    private final MissionRepository missionRepository;

    private static Random random = new Random();

    /**
     * 그룹의 시작시간에 알림이 전송될 회원을 반환합니다.
     * @param group 그룹
     * @return 그룹의 시작 시간에 알림이 전송될 회원
     */
    @Transactional
    public Member getMissionAllocatedMember(Group group){
        Member targetMember;
        if(group.getCurrentDoingMemberId() != null){// 1. 현재 수행중인 멤버가 있다면, 해당 멤버에게 미션 알림 보내기
            targetMember = memberRepository.findByIdAndStatus(group.getCurrentDoingMemberId(), Status.ACTIVE).orElseThrow(MemberNotFoundException::new);
        }else{// 2. 현재 수행중인 멤버가 없다면(처음 할당), 그룹 내 미션 수행 횟수가 가장 적은 사람 중에서 랜덤한 멤버에게 미션 할당 및 알림 보내기
            targetMember = getNextMissionMember(group);
            // 그룹의 현재 미션중인 멤버 ID 업데이트
            group.updateCurrentDoingMemberId(targetMember.getId());
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

    /**
     * 그룹 내에서 미션을 할당할 회원을 선정합니다.
     * @param group 그룹
     * @return 미션을 할당받을 회원
     */
    public Member getNextMissionMember(Group group){
        List<Member> minExecutedMemberList = joinListRepository.findMinExecutedMemberList(group);
        int randomIndex = random.nextInt(minExecutedMemberList.size());
        return minExecutedMemberList.get(randomIndex);
    }

    /**
     * 그룹에서 가장 릴레이 미션을 적게 수행한 사람 중 랜덤한 한 명에게 미션을 할당하고, 해당 유저를 반환합니다.
     * @param group 그룹
     * @param exerciseList 운동 리스트
     * @return 미션을 할당 받은 유저
     */
    @Transactional
    public Member allocateMission(Member member, Group group, List<Exercise> exerciseList){

        missionRepository.save(Mission.builder()
                .exercise(exerciseList.get(random.nextInt(exerciseList.size())))
                .member(member)
                .group(group)
                .build());

        log.info("그룹명 : {}, 그룹원 : {}, 할당 시각 : {}", group.getName(), member.getName(), LocalDateTime.now());
        return  member;
    }
}
