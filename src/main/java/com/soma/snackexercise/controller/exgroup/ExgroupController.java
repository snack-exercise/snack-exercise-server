package com.soma.snackexercise.controller.exgroup;


import com.soma.snackexercise.auth.oauth.CustomOAuth2User;
import com.soma.snackexercise.dto.exgroup.PostCreateExgroupRequest;
import com.soma.snackexercise.service.exgroup.ExgroupService;
import com.soma.snackexercise.util.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ExgroupController {

    private final ExgroupService exGroupService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/api/exgroups")
    public Response createGroup(@RequestBody PostCreateExgroupRequest groupCreateRequest, @AuthenticationPrincipal CustomOAuth2User loginUser){
        return Response.success(exGroupService.createGroup(groupCreateRequest, loginUser.getEmail()));
    }
}
