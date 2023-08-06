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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.soma.snackexercise.factory.entity.ExerciseFactory.createExercise;
import static com.soma.snackexercise.factory.entity.GroupFactory.*;
import static com.soma.snackexercise.factory.entity.JoinListFactory.createJoinListForMember;
import static com.soma.snackexercise.factory.entity.MemberFactory.createMember;
import static com.soma.snackexercise.factory.entity.MissionFactory.createNonCompleteMission;
import static com.soma.snackexercise.utils.ReflectionTestUtils.setCreatedAt;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MissionSchedulerService 단위 테스트")
class MissionSchedulerServiceUnitTest {
    @InjectMocks
    private MissionSchedulerService missionSchedulerService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JoinListRepository joinListRepository;

    @Mock
    private MissionUtil missionUtil;

    @Mock
    private FirebaseCloudMessageService firebaseCloudMessageService;

    @Test
    @DisplayName("미션 시작 스케줄러 동작 테스트")
    void allocateMissionAtGroupStartTimeTest() {
        // given
        Member member = createMember();
        Group group = createGroup();
        Exercise exercise = createExercise();

        List<Group> groupList = List.of(group);
        List<Exercise> exerciseList = List.of(exercise);

        given(groupRepository.findAllByStartDateNotNullAndIsGoalAchievedAndStatus(any(), any())).willReturn(groupList);
        given(exerciseRepository.findAll()).willReturn(exerciseList);
        given(missionUtil.getMissionAllocatedMember(any(Group.class))).willReturn(member);

        // when
        missionSchedulerService.allocateMissionAtGroupStartTime();

        // then
        verify(missionRepository, times(groupList.size())).save(any());
    }

    @Test
    @DisplayName("미션 할당 이후, 자동 독촉 알람 스케줄러 동작 테스트")
    void sendReminderNotificationsTest() throws Exception {
        // given
        final int checkIntervalTime = 10;

        Member targetMember = createMember();
        targetMember.updateFcmToken("token");

        Member member2 = createMember();
        member2.updateFcmToken("token");

        Group group = createGroupWithCheckIntervalTime(checkIntervalTime);
        group.updateCurrentDoingMemberId(1L);
        List<Group> groupList = List.of(group);

        JoinList joinList1 = createJoinListForMember(targetMember, group);
        JoinList joinList2 = createJoinListForMember(member2, group);
        List<JoinList> joinLists = List.of(joinList1, joinList2);

        Mission mission = createNonCompleteMission(createExercise(), targetMember, group);
        setCreatedAt(mission, LocalDateTime.now().minusMinutes(checkIntervalTime));

        given(groupRepository.findAllByIsGoalAchievedAndStatus(any(), any())).willReturn(groupList);
        given(memberRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.of(targetMember));
        given(missionRepository.findFirstByGroupAndMemberOrderByCreatedAtDesc(any(Group.class), any(Member.class))).willReturn(Optional.ofNullable(mission));
        given(joinListRepository.findByGroupAndStatus(any(Group.class), any())).willReturn(joinLists);

        // when
        missionSchedulerService.sendReminderNotifications();

        // then
        verify(firebaseCloudMessageService, times(1)).sendByToken(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("그룹 종료 스케줄러 동작 테스트")
    void inActiveGroupPastEndDateTest() {
        // given
        Member member = createMember();
        member.updateFcmToken("token");

        Group group = createGroupWithExistDays(0);
        List<Group> groupList = List.of(group);

        JoinList joinList = createJoinListForMember(member, group);
        List<JoinList> joinLists = List.of(joinList);

        given(groupRepository.findAllByEndDateGreaterThanAndStatus(any(), any())).willReturn(groupList);
        given(joinListRepository.findByGroupAndStatus(any(Group.class), any())).willReturn(joinLists);

        // when
        missionSchedulerService.inActiveGroupsPastEndDate();

        // then
        verify(firebaseCloudMessageService, times(1)).sendByTokenList(any(), anyString(), anyString());
    }
}