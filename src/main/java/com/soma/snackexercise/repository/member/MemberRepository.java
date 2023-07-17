package com.soma.snackexercise.repository.member;

import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.member.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 추가 정보를 입력받아 회원 가입을 진행할 때 소셜 타입, 식별자로 해당 회원을 찾기 위한 메소드
    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Optional<Member> findByEmail(String email);

    boolean existsByName(String name);
}
