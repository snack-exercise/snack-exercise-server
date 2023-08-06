package com.soma.snackexercise.service.signup;

import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.signup.SignupRequest;
import com.soma.snackexercise.exception.member.MemberNameAlreadyExistsException;
import com.soma.snackexercise.exception.member.MemberNotFoundException;
import com.soma.snackexercise.repository.member.MemberRepository;

import com.soma.snackexercise.util.constant.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class SignupService {
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    public void signup(SignupRequest signupRequest, HttpServletRequest request, HttpServletResponse response) {
        validateSignup(signupRequest);
        String email = jwtService.extractRefreshToken(request);
        Member findMember = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);
        findMember.signupMemberInfo(signupRequest.getNickname(), signupRequest.getGender(), signupRequest.getBirthYear());

        String newRefreshToken = jwtService.createRefreshToken(email);
        jwtService.sendRefreshToken(response, newRefreshToken);
        jwtService.updateRefreshToken(email, newRefreshToken);
    }

    private void validateSignup(SignupRequest request) {
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new MemberNameAlreadyExistsException(request.getNickname());
        }
    }
}
