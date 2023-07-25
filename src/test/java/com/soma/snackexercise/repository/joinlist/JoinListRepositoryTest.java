package com.soma.snackexercise.repository.joinlist;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.exception.JoinListNotFoundException;
import com.soma.snackexercise.repository.exgroup.ExgroupRepository;
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

import static com.soma.snackexercise.factory.entity.ExgroupFactory.createExgroup;
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
    private ExgroupRepository exgroupRepository;

    private Member member;
    private Exgroup exgroup;
    private JoinList joinList;
    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(createMember());
        exgroup = exgroupRepository.save(createExgroup());
        joinList = joinListRepository.save(createJoinListForMember(member, exgroup));
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
        Boolean exists = joinListRepository.existsByExgroupAndStatus(exgroup, Status.ACTIVE);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("그룹과 멤버와 Status 정보가 주어질 때 joinList가 조회되면 정상케이스이다.")
    void findByExgroupAndMemberAndStatusTest() {
        // given, when
        JoinList foundJoinList = joinListRepository.findByExgroupAndMemberAndStatus(exgroup, member, Status.ACTIVE).orElseThrow(JoinListNotFoundException::new);

        // then
        assertThat(joinList).isEqualTo(foundJoinList);
    }

    @Test
    @DisplayName("그룹과 Status 정보가 주어질 때 생성일 기준으로 오름차순 정렬하여 첫 번째 joinList를 반환하면 정상케이스이다.")
    void findFirstByExgroupAndStatusOrderByCreatedAtAscTest() {
        // given
        Member newMember = memberRepository.save(createMember());
        JoinList newJoinList = joinListRepository.save(createJoinListForMember(newMember, exgroup));
        clear();

        // when
        JoinList foundJoinList = joinListRepository.findFirstByExgroupAndStatusOrderByCreatedAtAsc(exgroup, Status.ACTIVE).orElseThrow(JoinListNotFoundException::new);

        // then
        assertThat(joinList.getCreatedAt()).isBefore(newJoinList.getCreatedAt());
        assertThat(foundJoinList.getId()).isEqualTo(joinList.getId());
        assertThat(foundJoinList.getId()).isNotEqualTo(newJoinList.getId());
    }

    @Test
    @DisplayName("그룹과 멤버와 유형과 Status가 주어질 때 존재한다면 정상케이스이다.")
    void existsByExgroupAndMemberAndJoinTypeAndStatusTest() {
        // given, when
        Boolean exists = joinListRepository.existsByExgroupAndMemberAndJoinTypeAndStatus(exgroup, member, JoinType.MEMBER, Status.ACTIVE);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("그룹의 현재 인원수를 판단하는 테이스케이스이다.")
    void countByExgroupAndOutCountLessThanOneAndStatusEqualsActiveTest() {
        // given
        Member newMember = memberRepository.save(createMember());
        JoinList newJoinList = joinListRepository.save(createJoinListForMember(newMember, exgroup));
        clear();

        // when
        Integer count = joinListRepository.countByExgroupAndOutCountLessThanOneAndStatusEqualsActive(exgroup);

        // then
        assertThat(count).isEqualTo(2);
    }

    void clear() {
        entityManager.flush();
        entityManager.clear();
    }
}