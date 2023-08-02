package com.soma.snackexercise.repository.joinlist;

import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.exception.JoinListNotFoundException;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.util.constant.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.soma.snackexercise.factory.entity.GroupFactory.createGroup;
import static com.soma.snackexercise.factory.entity.JoinListFactory.createJoinListForMember;
import static com.soma.snackexercise.factory.entity.MemberFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DisplayName("JoinListRepository JPA 동작 테스트")
class JoinListRepositoryTest {
    @Autowired
    private JoinListRepository joinListRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupRepository groupRepository;

    private Member member;
    private Group group;
    private JoinList joinList;
    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(createMember());
        group = groupRepository.save(createGroup());
        joinList = joinListRepository.save(createJoinListForMember(member, group));
        clear();
    }

    @Test
    @DisplayName("멤버가 주어질 때 joinList가 삭제 되면 정상케이스이다.")
    void deleteByMemberTest() {
        // given, when
        joinListRepository.deleteByMember(member);
        clear();

        // then
        List<JoinList> result = joinListRepository.findAll();
        assertThat(result.size()).isZero();
    }

    @Test
    @DisplayName("그룹과 Status가 주어질 때 joinList가 있다면 참을 반환하면 정상케이스이다.")
    void existsByExgroupAndStatusTest() {
        // given, when
        Boolean exists = joinListRepository.existsByGroupAndStatus(group, Status.ACTIVE);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("그룹과 멤버와 Status 정보가 주어질 때 joinList가 조회되면 정상케이스이다.")
    void findByExgroupAndMemberAndStatusTest() {
        // given, when
        JoinList foundJoinList = joinListRepository.findByGroupAndMemberAndStatus(group, member, Status.ACTIVE).orElseThrow(JoinListNotFoundException::new);

        // then
        assertThat(joinList.getId()).isEqualTo(foundJoinList.getId());
    }

    @Test
    @DisplayName("그룹과 Status 정보가 주어질 때 생성일 기준으로 오름차순 정렬하여 첫 번째 joinList를 반환하면 정상케이스이다.")
    void findFirstByExgroupAndStatusOrderByCreatedAtAscTest() {
        // given
        Member newMember = memberRepository.save(createMember());
        JoinList newJoinList = joinListRepository.save(createJoinListForMember(newMember, group));
        clear();

        // when
        JoinList foundJoinList = joinListRepository.findFirstByGroupAndStatusOrderByCreatedAtAsc(group, Status.ACTIVE).orElseThrow(JoinListNotFoundException::new);

        // then
        assertThat(joinList.getCreatedAt()).isBefore(newJoinList.getCreatedAt());
        assertThat(foundJoinList.getId()).isEqualTo(joinList.getId());
        assertThat(foundJoinList.getId()).isNotEqualTo(newJoinList.getId());
    }

    @Test
    @DisplayName("그룹과 멤버와 유형과 Status가 주어질 때 존재한다면 정상케이스이다.")
    void existsByExgroupAndMemberAndJoinTypeAndStatusTest() {
        // given, when
        Boolean exists = joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(group, member, JoinType.MEMBER, Status.ACTIVE);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("그룹의 현재 인원수를 판단하는 테이스케이스이다.")
    void countByExgroupAndOutCountLessThanOneAndStatusEqualsActiveTest() {
        // given
        Member newMember = memberRepository.save(createMember());
        JoinList newJoinList = joinListRepository.save(createJoinListForMember(newMember, group));
        clear();

        // when
        Integer count = joinListRepository.countByGroupAndOutCountLessThanOneAndStatusEqualsActive(group);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("그룹과 멤버와 상태가 주어질 때 joinList의 존재 여부를 확인하는 테스트")
    void existsByGroupAndMemberAndStatusTest() {
        // given
        Member member1 = memberRepository.save(createMember());
        Member member2 = memberRepository.save(createMember());
        Group group = groupRepository.save(createGroup());
        joinListRepository.save(createJoinListForMember(member1, group));
        JoinList joinList = joinListRepository.save(createJoinListForMember(member2, group));
        joinList.inActive();
        clear();

        // when
        Boolean exists = joinListRepository.existsByGroupAndMemberAndStatus(group, member1, Status.ACTIVE);
        Boolean notExists = joinListRepository.existsByGroupAndMemberAndStatus(group, member2, Status.ACTIVE);


        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("그룹과 회원과 강퇴 횟수와 상태가 주어진 조건의 joinList의 존재 여부를 확인하는 테스트")
    void existsByGroupAndMemberAndOutCountGreaterThanEqualAndStatus() {
        // given
        joinList.addOneOutCount();
        joinList.addOneOutCount();
        joinListRepository.save(joinList);
        int outCount = joinList.getOutCount();

        clear();

        // when
        Boolean exists = joinListRepository.existsByGroupAndMemberAndOutCountGreaterThanEqualAndStatus(group, member, outCount, Status.ACTIVE);
        Boolean notExists = joinListRepository.existsByGroupAndMemberAndOutCountGreaterThanEqualAndStatus(group, member, outCount + 1, Status.ACTIVE);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void findMaxExecutedMissionCountByGroupAndStatusTest() {
        joinList = joinListRepository.save(createJoinListForMember(member, group));
        joinList.addOneExecutedMissionCountCount();
        joinList.addOneExecutedMissionCountCount();
        joinListRepository.save(joinList);

        Member member1 = memberRepository.save(createMember());
        JoinList joinList1 = joinListRepository.save(createJoinListForMember(member1, group));
        joinList1.addOneExecutedMissionCountCount();
        joinList1.addOneExecutedMissionCountCount();
        joinListRepository.save(joinList1);

        clear();

        int cnt = joinListRepository.findCurrentRoundPositionByGroupId(group, Status.ACTIVE);
        assertThat(cnt).isEqualTo(2);
    }

    void clear() {
        entityManager.flush();
        entityManager.clear();
    }
}