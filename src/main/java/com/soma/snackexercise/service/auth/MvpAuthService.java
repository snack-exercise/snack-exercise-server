package com.soma.snackexercise.service.auth;

import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.auth.MvpLoginRequest;
import com.soma.snackexercise.dto.auth.MvpLoginResponse;
import com.soma.snackexercise.exception.MemberNicknameAlreadyExistsException;
import com.soma.snackexercise.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MvpAuthService {
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    public MvpLoginResponse login(MvpLoginRequest request) {
        String email = UUID.randomUUID() + "@socialUser.com";

        validateDuplicateNickname(request);

        Member member = Member.builder()
                .email(email)
                .nickname(request.getNickname())
                .build();

        memberRepository.save(member);

        String accessToken = jwtService.createAccessToken(email);

        return new MvpLoginResponse("Bearer " + accessToken, member.getId());
    }

    private void validateDuplicateNickname(MvpLoginRequest request) {
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new MemberNicknameAlreadyExistsException();
        }
    }
}
