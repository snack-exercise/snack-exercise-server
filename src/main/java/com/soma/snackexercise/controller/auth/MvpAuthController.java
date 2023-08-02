package com.soma.snackexercise.controller.auth;


import com.soma.snackexercise.dto.auth.MvpLoginRequest;
import com.soma.snackexercise.service.auth.MvpAuthService;
import com.soma.snackexercise.util.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mvp/auth")
public class MvpAuthController {
    private final MvpAuthService mvpAuthService;
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Response login(@RequestBody  MvpLoginRequest request) {
        return Response.success(mvpAuthService.login(request));
    }
}
