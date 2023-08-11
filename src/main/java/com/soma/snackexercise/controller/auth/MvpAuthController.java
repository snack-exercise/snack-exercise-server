package com.soma.snackexercise.controller.auth;


import com.soma.snackexercise.dto.auth.MvpLoginRequest;
import com.soma.snackexercise.service.auth.MvpAuthService;
import com.soma.snackexercise.util.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mvp/auth")
public class MvpAuthController {
    private final MvpAuthService mvpAuthService;
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.OK)
    public Response signup(@RequestBody @Valid MvpLoginRequest request) {
        return Response.success(mvpAuthService.signup(request));
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Response login(@RequestBody @Valid MvpLoginRequest request) {
        return Response.success(mvpAuthService.login(request));
    }
}
