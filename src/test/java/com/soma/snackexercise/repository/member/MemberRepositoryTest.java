package com.soma.snackexercise.repository.member;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.member.SocialType;
import com.soma.snackexercise.dto.member.JoinListMemberDto;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.repository.exgroup.ExgroupRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.util.constant.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.soma.snackexercise.factory.entity.ExgroupFactory.createExgroup;
import static com.soma.snackexercise.factory.entity.JoinListFactory.createJoinListForMember;
import static com.soma.snackexercise.factory.entity.MemberFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DisplayName("MemberRepository JPA 동작 테스트")
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ExgroupRepository exgroupRepository;

    @Autowired
    private JoinListRepository joinListRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Exgroup exgroup;
    private JoinList joinList;

    @Test
    @DisplayName("소셜 타입과 소셜 id로 멤버를 조회하는 테스트")
    void findBySocialTypeAndSocialIdTest() {
        // given
        SocialType socialType = SocialType.KAKAO;
        String socialId = "1234";
        Member member = memberRepository.save(createMemberWithSocialTypeAndSocialId(socialType, socialId));
        clear();

        // when
        Member foundMember = memberRepository.findBySocialTypeAndSocialIdAndStatus(socialType, socialId, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);

        // then
        assertThat(foundMember.getId()).isEqualTo(member.getId());
        assertThat(foundMember.getSocialId()).isEqualTo(member.getSocialId());
    }

    @Test
    @DisplayName("멤버 이메일을 통해 멤버를 찾는 테스트")
    void findByEmailAndStatusTest() {
        // given
        String email = "test@naver.com";
        Member member = memberRepository.save(createMemberWithEmail(email));
        clear();

        // when
        Member foundMember = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);

        // that
        assertThat(foundMember.getId()).isEqualTo(member.getId());
        assertThat(foundMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("닉네임 중복에서 참을 반환하는 테스트")
    void existsByNameTest() {
        // given
        String duplicateNickname = "duplicate";
        String uniqueNickname = "unique";
        Member member = memberRepository.save(createMemberWithNickname(duplicateNickname));
        clear();

        // when
        Boolean isDuplicate = memberRepository.existsByNickname(duplicateNickname);
        Boolean isUnique = memberRepository.existsByNickname(uniqueNickname);

        // then
        assertThat(isDuplicate).isTrue();
        assertThat(isUnique).isFalse();
    }

    @Test
    @DisplayName("그룹에 속한 멤버 정보의 리스트를 알맞게 조회하는지 테스트")
    void findAllGroupMembersTest() {
        // given
        Member member1 = memberRepository.save(createMember());
        Member member2 = memberRepository.save(createMember());
        exgroup = exgroupRepository.save(createExgroup());
        joinList = joinListRepository.save(createJoinListForMember(member1, exgroup));
        joinList = joinListRepository.save(createJoinListForMember(member2, exgroup));
        clear();

        // when
        List<JoinListMemberDto> joinListMemberDtos = memberRepository.findAllGroupMembers(exgroup.getId());

        // then
        assertThat(joinListMemberDtos).hasSize(2);
        assertThat(joinListMemberDtos.stream().map(dto -> dto.getMember().getId())).containsExactlyInAnyOrder(member1.getId(), member2.getId());
    }

    @Test
    @DisplayName("멤버 id로 멤버를 조회하는 테스트")
    void findByIdAndStatus() {
        // given
        Member member = memberRepository.save(createMember());
        clear();

        // when
        Member foundMember = memberRepository.findByIdAndStatus(member.getId(), Status.ACTIVE).orElseThrow(MemberNotFoundException::new);

        // then
        assertThat(member.getId()).isEqualTo(foundMember.getId());
        assertThatThrownBy(() -> memberRepository.findByIdAndStatus(member.getId(), Status.INACTIVE).orElseThrow(MemberNotFoundException::new)).isInstanceOf(MemberNotFoundException.class);
    }


    void clear() {
        entityManager.flush();
        entityManager.clear();
    }
}
