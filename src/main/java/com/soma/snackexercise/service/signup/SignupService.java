package com.soma.snackexercise.service.signup;

import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.signup.SignupRequest;
import com.soma.snackexercise.exception.MemberNameAlreadyExistsException;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class SignupService {
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    public void signup(SignupRequest request, String email) {
        validateSignup(request);
        Member findMember = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        findMember.signupMemberInfo(request.getName(), request.getGender(), request.getBirthYear());
    }

    private void validateSignup(SignupRequest request) {
        if (memberRepository.existsByName(request.getName())) {
            throw new MemberNameAlreadyExistsException(request.getName());
        }
    }


}
