package com.soma.snackexercise.repository.member;

import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.member.SocialType;
import com.soma.snackexercise.dto.member.JoinListMemberDto;
import com.soma.snackexercise.util.constant.Status;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
// TODO : 모두 Status 조건 걸기
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 추가 정보를 입력받아 회원 가입을 진행할 때 소셜 타입, 식별자로 해당 회원을 찾기 위한 메소드
    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Optional<Member> findByEmail(String email);

    boolean existsByName(String name);

    @Query("SELECT new com.soma.snackexercise.dto.member.JoinListMemberDto(m, jl) FROM JoinList jl JOIN FETCH Member m ON jl.member = m WHERE jl.exgroup.id = :groupId")
    List<JoinListMemberDto> findAllGroupMembers(@Param("groupId") Long groupId);

    Optional<Member> findByIdAndStatus(Long id, Status status);
}
