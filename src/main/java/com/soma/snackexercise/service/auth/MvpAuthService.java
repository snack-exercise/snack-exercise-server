package com.soma.snackexercise.service.auth;

import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.auth.MvpLoginRequest;
import com.soma.snackexercise.dto.auth.MvpLoginResponse;
import com.soma.snackexercise.exception.member.MemberNicknameAlreadyExistsException;
import com.soma.snackexercise.exception.member.MemberNotFoundException;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.service.notification.NotificationService;
import com.soma.snackexercise.util.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MvpAuthService {
    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final NotificationService notificationService;

    @Transactional
    public MvpLoginResponse signup(MvpLoginRequest request) {
        String email = UUID.randomUUID() + "@socialUser.com";

        validateDuplicateNickname(request);

        Member member = Member.builder()
                .email(email)
                .nickname(request.getNickname())
                .build();
        // fcm 토큰 저장
        member.updateFcmToken(request.getFcmToken());

        memberRepository.save(member);

        String accessToken = jwtService.createAccessToken(email);

        return new MvpLoginResponse("Bearer " + accessToken, member.getId());
    }

    @Transactional
    public MvpLoginResponse login(MvpLoginRequest request) {
        Member member = memberRepository.findByNicknameAndStatus(request.getNickname(), Status.ACTIVE).orElseThrow(MemberNotFoundException::new);

        String accessToken = jwtService.createAccessToken(member.getEmail());

        // fcm 토큰 저장
        member.updateFcmToken(request.getFcmToken());

        return new MvpLoginResponse("Bearer " + accessToken, member.getId());
    }

    private void validateDuplicateNickname(MvpLoginRequest request) {
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new MemberNicknameAlreadyExistsException();
        }
    }
}
