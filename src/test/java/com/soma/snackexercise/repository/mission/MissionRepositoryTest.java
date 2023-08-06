package com.soma.snackexercise.repository.mission;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.repository.exercise.ExerciseRepository;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.soma.snackexercise.factory.entity.ExerciseFactory.createExercise;
import static com.soma.snackexercise.factory.entity.GroupFactory.createGroup;
import static com.soma.snackexercise.factory.entity.MemberFactory.createMember;
import static com.soma.snackexercise.factory.entity.MissionFactory.createCompleteMission;
import static com.soma.snackexercise.factory.entity.MissionFactory.createNonCompleteMission;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DisplayName("MissionRepository JPA 동작 테스트")
class MissionRepositoryTest {
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Group group;
    private Member member;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(createMember());
        group = groupRepository.save(createGroup());
        exercise = exerciseRepository.save(createExercise());
    }

    @Test
    @DisplayName("그룹 id와 기간이 주어지면 기간 안에 완료한 릴레이 횟수를 맞게 반환하는지 검증하는 테스트")
    void findCurrentFinishedRelayCountByGroupIdTest() {
        // given
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);// 오늘 자정 구일하기
        LocalDateTime nextDay = today.plusDays(1);// 내일 자정 구하기
        Mission mission1 = missionRepository.save(createCompleteMission(exercise, member, group));
        Mission mission2 = missionRepository.save(createCompleteMission(exercise, member, group));
        clear();

        // when
        Integer count = missionRepository.findCurrentFinishedRelayCountByGroupId(group.getId(), today, nextDay);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("그룹 id와 기간이 주어지면 기간 안의 모든 미션들을 맞게 반환하는지 검증하는 테스트")
    void findAllMissionByGroupIdAndCreatedAtTest() {
        // given
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);// 오늘 자정 구일하기
        LocalDateTime nextDay = today.plusDays(1);// 내일 자정 구하기
        Mission completeMission = missionRepository.save(createCompleteMission(exercise, member, group));
        Mission nonCompleteMission = missionRepository.save(createNonCompleteMission(exercise, member, group));
        clear();

        // when
        List<Mission> missions = missionRepository.findMissionsByGroupIdWithinDateRange(group.getId(), today, nextDay);

        // then
        assertThat(missions.size()).isEqualTo(2);
        assertThat(missions.stream().map(Mission::getId))
                .containsExactlyInAnyOrder(completeMission.getId(), nonCompleteMission.getId());
    }

    @Test
    @DisplayName("그룹 id와 기간이 주어지면 기간 안의 모든 완료한 미션들을 맞게 반환하는지 검증하는 테스트")
    void findAllExecutedMissionByGroupIdAndCreatedAtTest() {
        // given
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);// 오늘 자정 구일하기
        LocalDateTime nextDay = today.plusDays(1);// 내일 자정 구하기
        Mission completeMission = missionRepository.save(createCompleteMission(exercise, member, group));
        Mission nonCompleteMission = missionRepository.save(createNonCompleteMission(exercise, member, group));
        clear();

        // when
        List<Mission> missions = missionRepository.findFinishedMissionsByGroupIdWithinDateRange(group.getId(), today, nextDay);

        // then
        assertThat(missions.size()).isEqualTo(1);
        assertThat(missions.stream().map(Mission::getId))
                .containsExactlyInAnyOrder(completeMission.getId());
    }

    void clear() {
        entityManager.flush();
        entityManager.clear();
    }
}