package com.soma.snackexercise.controller.signup;

import com.soma.snackexercise.dto.signup.SignupRequest;
import com.soma.snackexercise.service.signup.SignupService;
import com.soma.snackexercise.util.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SignupController {
    private final SignupService signupService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signup(@RequestBody SignupRequest signupRequest, HttpServletRequest request, HttpServletResponse response) {
        signupService.signup(signupRequest, request, response);
        return Response.success();
    }
}
