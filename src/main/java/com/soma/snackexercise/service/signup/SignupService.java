package com.soma.snackexercise.service.signup;

import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.signup.SignupRequest;
import com.soma.snackexercise.exception.MemberNameAlreadyExistsException;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.repository.member.MemberRepository;

import com.soma.snackexercise.util.constant.Status;
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

    public void signup(SignupRequest request, HttpServletResponse response, String email) {
        validateSignup(request);
        Member findMember = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);
        findMember.signupMemberInfo(request.getName(), request.getGender(), request.getBirthYear());

        String newRefreshToken = jwtService.createRefreshToken(email);
        jwtService.sendRefreshToken(response, newRefreshToken);
        jwtService.updateRefreshToken(email, newRefreshToken);
    }

    private void validateSignup(SignupRequest request) {
        if (memberRepository.existsByName(request.getName())) {
            throw new MemberNameAlreadyExistsException(request.getName());
        }
    }
}
