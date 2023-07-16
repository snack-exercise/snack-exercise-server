package com.soma.snackexercise.controller.exgroup;


import com.soma.snackexercise.dto.exgroup.PostCreateExgroupRequest;
import com.soma.snackexercise.service.exgroup.ExgroupService;
import com.soma.snackexercise.util.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ExgroupController {

    private final ExgroupService exGroupService;
    @PostMapping("/api/exgroups")
    public Response createGroup(@RequestBody PostCreateExgroupRequest groupCreateRequest, @AuthenticationPrincipal UserDetails loginUser){
        return Response.success(exGroupService.createGroup(groupCreateRequest, loginUser.getUsername()));
    }
}
