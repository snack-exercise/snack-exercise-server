package com.soma.snackexercise.service.notification;

import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.member.request.MemberUpdateFcmTokenRequest;
import com.soma.snackexercise.dto.member.response.MemberUpdateFcmTokenResponse;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.util.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NotificationService {
    private final MemberRepository memberRepository;

    @Transactional
    public MemberUpdateFcmTokenResponse updateFcmToken(MemberUpdateFcmTokenRequest request, String email){
        Member member = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);
        member.updateFcmToken(request.getFcmToken());
        return MemberUpdateFcmTokenResponse.toDto(member);
    }
}
