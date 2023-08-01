package com.soma.snackexercise.controller.signup;

import com.soma.snackexercise.dto.signup.SignupRequest;
import com.soma.snackexercise.service.signup.SignupService;
import com.soma.snackexercise.util.response.Response;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin(originPatterns = "http://localhost:3000")
public class SignupController {
    private final SignupService signupService;

    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signup(@RequestBody SignupRequest request, HttpServletResponse response, @AuthenticationPrincipal UserDetails loginUser) {
        signupService.signup(request, response, loginUser.getUsername());
        return Response.success();
    }
}
