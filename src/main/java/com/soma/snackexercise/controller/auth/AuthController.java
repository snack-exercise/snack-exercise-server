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
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/auth/reissue")
    @ResponseStatus(HttpStatus.OK)
    public Response reissue(HttpServletRequest request, HttpServletResponse response) {
        return Response.success(authService.reissue(request, response));
    }

    @PostMapping("/api/auth/logout")
    @ResponseStatus(HttpStatus.OK)
    public Response logout(HttpServletRequest request) {
        authService.logout(request);
        return Response.success();
    }
}
