package com.soma.snackexercise.controller.auth;

import com.soma.snackexercise.service.auth.AuthService;
import com.soma.snackexercise.util.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public Response reissue(HttpServletRequest request, HttpServletResponse response) {
        authService.reissue(request, response);
        return Response.success();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public Response logout(HttpServletRequest request) {
        authService.logout(request);
        return Response.success();
    }
}
