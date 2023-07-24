package com.soma.snackexercise.service.member;

import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.member.request.MemberUpdateRequest;
import com.soma.snackexercise.dto.member.response.MemberResponse;
import com.soma.snackexercise.exception.MemberNameAlreadyExistsException;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.util.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse update(Long id, MemberUpdateRequest request) {
        Member member = memberRepository.findByIdAndStatus(id, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);

        if (!member.getName().equals(request.getName())) {
            validateDuplicateName(request.getName());
        }

        member.update(request);
        return MemberResponse.toDto(member);
    }

    private void validateDuplicateName(String name) {
        if (memberRepository.existsByName(name)) {
            throw new MemberNameAlreadyExistsException(name);
        }
    }


    // TODO : 탈퇴한 회원이 다시 그룹 가입하면
    /*
    회원 탈퇴하기
    그룹이 존재하면? -> joinlist 모두 inActive로 변경
    관련 알림 모두 inActive
    관련 회원_운동 inActive


    회원_운동 데이터는?
     */
}
