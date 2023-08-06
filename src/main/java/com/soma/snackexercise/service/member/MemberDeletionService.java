package com.soma.snackexercise.service.member;

import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.repository.mission.MissionRepository;
import com.soma.snackexercise.util.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
단일 책임 원칙에 기반하여 의존성이 많은 서비스 클래스를 따로 분리
 */
@Service
@RequiredArgsConstructor
public class MemberDeletionService {
    private final MemberRepository memberRepository;
    private final MissionRepository missionRepository;
    // TODO : 알림 관련 정보와 운동_회원도 삭제
    // TODO : 2번 강퇴당한 회원이 다시 재가입할 수 있는 문제
    private final JoinListRepository joinListRepository;

    @Transactional
    public void deleteMember(String email) {
        Member member = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);

        missionRepository.deleteByMember(member);

        joinListRepository.deleteByMember(member);
    }
}
