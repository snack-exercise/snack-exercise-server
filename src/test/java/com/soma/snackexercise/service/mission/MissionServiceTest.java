package com.soma.snackexercise.service.mission;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.dto.mission.response.MemberMissionDto;
import com.soma.snackexercise.dto.mission.response.RankingResponse;
import com.soma.snackexercise.dto.mission.response.TodayMissionResultResponse;
import com.soma.snackexercise.exception.ExgroupNotFoundException;
import com.soma.snackexercise.repository.exgroup.ExgroupRepository;
import com.soma.snackexercise.repository.mission.MissionRepository;
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
import static com.soma.snackexercise.factory.entity.ExgroupFactory.createExgroup;
import static com.soma.snackexercise.factory.entity.MemberFactory.createMember;
import static com.soma.snackexercise.factory.entity.MemberFactory.createMemberWithIdAndNickname;
import static com.soma.snackexercise.factory.entity.MissionFactory.createCompleteMission;
import static com.soma.snackexercise.factory.entity.MissionFactory.createNonCompleteMission;
import static com.soma.snackexercise.utils.ReflectionTestUtils.setCreatedAt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MissionService 비즈니스 로직 테스트")
class MissionServiceTest {

    @InjectMocks
    private MissionService missionService;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private ExgroupRepository exgroupRepository;

    @Test
    @DisplayName("오늘의 미션 결과를 조회하는 메소드 성공 테스트")
    void readTodayMissionResultsTest() {
        // given
        Member member1 = createMember();
        Member member2 = createMember();
        Mission completeMission = createCompleteMission(createExercise(), member1, createExgroup());
        Mission nonCompleteMission = createNonCompleteMission(createExercise(), member2, createExgroup());

        given(exgroupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(createExgroup()));
        given(missionRepository.findMissionsByGroupIdWithinDateRange(anyLong(), any(), any())).willReturn(List.of(completeMission, nonCompleteMission));

        // when
        TodayMissionResultResponse response = missionService.readTodayMissionResults(1L);

        // then
        assertThat(response.getMissionFlow().stream().map(MemberMissionDto::getMemberName)).containsExactlyInAnyOrder(member1.getNickname(), member2.getNickname());
    }

    @Test
    @DisplayName("오늘의 미션 순위를 조회하는 메소드 성공 테스트")
    void readTodayMissionRankTest() throws Exception {
        // given
        Member member1 = createMemberWithIdAndNickname(1L, "member1");
        Member member2 = createMemberWithIdAndNickname(2L, "member2");

        Mission completeMission1 = createCompleteMission(createExercise(), member1, createExgroup());
        Mission completeMission2 = createCompleteMission(createExercise(), member2, createExgroup());

        setCreatedAt(completeMission1, LocalDateTime.now());
        setCreatedAt(completeMission2, LocalDateTime.now().minusMinutes(10));

        given(exgroupRepository.existsByIdAndStatus(anyLong(), any())).willReturn(true);
        given(missionRepository.findFinishedMissionsByGroupIdWithinDateRange(anyLong(), any(), any())).willReturn(List.of(completeMission1, completeMission2));

        // when
        List<RankingResponse> response = (List<RankingResponse>) missionService.readTodayMissionRank(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(2);
        assertThat(response.stream().map(RankingResponse::getNickname)).containsExactly(member1.getNickname(), member2.getNickname());
    }

    @Test
    @DisplayName("오늘의 미션 순위를 조회하는 메소드에서 그룹 조회 예외 클래스 발생 테스트")
    void readTodayMissionRankExgroupNotFoundExceptionTest() throws Exception {
        // given
        given(exgroupRepository.existsByIdAndStatus(anyLong(), any())).willReturn(false);

        // when, then
        assertThatThrownBy(() -> missionService.readTodayMissionRank(1L)).isInstanceOf(ExgroupNotFoundException.class);
    }

    @Test
    @DisplayName("누적 미션 순위를 조회하는 메소드 성공 테스트")
    void readCumulativeMissionRankTest() throws Exception {
        // given
        Member member1 = createMemberWithIdAndNickname(1L, "member1");
        Member member2 = createMemberWithIdAndNickname(2L, "member2");
        Exgroup exgroup = createExgroup();
        exgroup.updateStartDateAndEndDate();

        Mission completeMission1 = createCompleteMission(createExercise(), member1, exgroup);
        Mission completeMission2 = createCompleteMission(createExercise(), member2, exgroup);

        setCreatedAt(completeMission1, LocalDateTime.now());
        setCreatedAt(completeMission2, LocalDateTime.now().minusMinutes(10));

        given(exgroupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(exgroup));
        given(missionRepository.findFinishedMissionsByGroupIdWithinDateRange(anyLong(), any(), any())).willReturn(List.of(completeMission1, completeMission2));

        // when
        List<RankingResponse> response = (List<RankingResponse>) missionService.readCumulativeMissionRank(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(2);
        assertThat(response.stream().map(RankingResponse::getNickname)).containsExactly(member1.getNickname(), member2.getNickname());
    }

    @Test
    @DisplayName("누적 미션 순위를 조회하는 메소드에서 그룹 조회 예외 클래스 발생 테스트")
    void readCumulativeMissionRankExgroupNotFoundExceptionTest() throws Exception {
        // given
        given(exgroupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> missionService.readCumulativeMissionRank(1L)).isInstanceOf(ExgroupNotFoundException.class);
    }

    @Test
    @DisplayName("누적 미션 순위를 조회하는 메소드에서 미션이 없는 경우 빈 리스트 반환 테스트")
    void readCumulativeMissionRankReturnsEmptyListWhenNoMissions() throws Exception {
        // given
        Exgroup exgroup = createExgroup();
        exgroup.updateStartDateAndEndDate();

        given(exgroupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(exgroup));
        given(missionRepository.findFinishedMissionsByGroupIdWithinDateRange(anyLong(), any(), any())).willReturn(List.of());

        // when
        List<RankingResponse> response = (List<RankingResponse>) missionService.readCumulativeMissionRank(1L);

        // then
        assertTrue(response.isEmpty());
    }
}