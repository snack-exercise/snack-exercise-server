package com.soma.snackexercise.service.mission;

import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.repository.exercise.ExerciseRepository;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.repository.mission.MissionRepository;
import com.soma.snackexercise.util.constant.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.soma.snackexercise.factory.entity.ExerciseFactory.createExercise;
import static com.soma.snackexercise.factory.entity.GroupFactory.createGroupWithStartTime;
import static com.soma.snackexercise.factory.entity.MemberFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MissionScheulerServiceIntegrationTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MissionUtil missionUtil;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private MissionSchedulerService missionSchedulerService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("그룹 시작 시간 미션 할당 스케줄러 테스트")
    void allocateMissionAtGroupStartTimeTest() {
        // given
        Member targetMember = createMember();
        targetMember.updateFcmToken("token");
        Group group = createGroupWithStartTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        group.updateCurrentDoingMemberId(targetMember.getId());
        group.updateStartDateAndEndDate();

        memberRepository.save(targetMember);
        exerciseRepository.save(createExercise());
        groupRepository.save(group);

        clear();

        List<Group> groupList = groupRepository.findAllByStartDateNotNullAndIsGoalAchievedAndStatus(false, Status.ACTIVE);
        System.out.println("test : " + groupList.size());

        // when
        missionSchedulerService.allocateMissionAtGroupStartTime();

        // then
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).isNotEmpty();
        assertThat(missionList.get(0).getGroup().getName()).isEqualTo(group.getName());
    }

    void clear() {
        entityManager.flush();
        entityManager.clear();
    }

}
