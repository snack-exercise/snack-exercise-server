package com.soma.snackexercise.controller.signup;

import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.dto.signup.SignupRequest;
import com.soma.snackexercise.service.member.MemberService;
import com.soma.snackexercise.service.signup.SignupService;
import com.soma.snackexercise.util.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SignupController {
    private final SignupService signupService;
    private final JwtService jwtService;

    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signup(@RequestBody SignupRequest request, @AuthenticationPrincipal UserDetails loginUser, HttpServletResponse response) {
        signupService.signup(request, loginUser.getUsername());
        jwtService.sendAccessAndRefreshByEmail(response, loginUser.getUsername());
        return Response.success();
    }
}
