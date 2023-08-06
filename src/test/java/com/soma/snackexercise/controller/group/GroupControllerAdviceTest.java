package com.soma.snackexercise.controller.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soma.snackexercise.advice.ExceptionAdvice;
import com.soma.snackexercise.dto.group.request.GroupCreateRequest;
import com.soma.snackexercise.dto.group.request.GroupUpdateRequest;
import com.soma.snackexercise.dto.group.request.JoinFriendGroupRequest;
import com.soma.snackexercise.exception.group.*;
import com.soma.snackexercise.exception.joinlist.JoinListNotFoundException;
import com.soma.snackexercise.exception.member.MemberNotFoundException;
import com.soma.snackexercise.service.group.GroupService;
import com.soma.snackexercise.utils.TestUserArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.soma.snackexercise.advice.ErrorCode.*;
import static com.soma.snackexercise.factory.dto.GroupCreateFactory.createGroupCreateRequest;
import static com.soma.snackexercise.factory.dto.GroupCreateFactory.createJoinFriendGroupRequest;
import static com.soma.snackexercise.factory.dto.GroupUpdateFactory.createGroupUpdateRequest;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GroupControllerAdviceTest {
    @InjectMocks
    private GroupController groupController;

    @Mock
    private GroupService groupService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController)
                .setCustomArgumentResolvers(new TestUserArgumentResolver())
                .setControllerAdvice(new ExceptionAdvice())
                .build();
    }

    @Test
    @DisplayName("운동 그룹 생성 메소드에서 MemberNotFoundException 발생시 적절한 응답을 반환하는지 테스트")
    void createMemberNotFoundExceptionTest() throws Exception {
        // given
        GroupCreateRequest request = createGroupCreateRequest();
        given(groupService.create(any(), anyString())).willThrow(MemberNotFoundException.class);

        // when, then
        mockMvc.perform(
                        post("/groups")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(MEMBER_NOT_FOUND_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("운동 그룹 조회 메소드에서 GroupNotFoundExceptionTest 발생시 적절한 응답을 반환하는지 테스트")
    void readExgroupNotFoundExceptionTest() throws Exception {
        // given
        Long groupId = 1L;
        given(groupService.read(anyLong())).willThrow(GroupNotFoundException.class);

        // when, then
        mockMvc.perform(
                        get("/groups/{groupId}", groupId)
                ).andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(GROUP_NOT_FOUND_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(GROUP_NOT_FOUND_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("운동 그룹 수정 메소드에서 NotExgroupHostException 발생시 적절한 응답을 반환하는지 테스트")
    void updateNotExgroupHostExceptionTest() throws Exception {
        // given
        Long groupId = 1L;
        GroupUpdateRequest request = createGroupUpdateRequest();
        given(groupService.update(anyLong(), anyString(), any())).willThrow(NotGroupHostException.class);

        // when, then
        mockMvc.perform(
                        patch("/groups/{groupId}", groupId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(NOT_GROUP_HOST_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(NOT_GROUP_HOST_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("운동 그룹에서 방장이 회원 강퇴 메소드에서 JoinListNotFoundException 발싱시 적절한 응답을 반환하는지 테스트")
    void deleteMemberByHostJoinListNotFoundExceptionTest() throws Exception {
        // given
        Long groupId = 1L;
        Long memberId = 1L;
        doThrow(JoinListNotFoundException.class).when(groupService).deleteMemberByHost(anyLong(), anyLong(), anyString());

        // when, then
        mockMvc.perform(
                        delete("/groups/{groupId}/members/{memberId}", groupId, memberId)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(JOIN_LIST_NOT_FOUND_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(JOIN_LIST_NOT_FOUND_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("운동 그룹에서 방장이 회원 강퇴 메소드에서 NotExgroupMemberException 발싱시 적절한 응답을 반환하는지 테스트")
    void deleteMemberByHostNotExgroupMemberExceptionExceptionTest() throws Exception {
        // given
        Long groupId = 1L;
        Long memberId = 1L;
        doThrow(NotGroupMemberException.class).when(groupService).deleteMemberByHost(anyLong(), anyLong(), anyString());

        // when, then
        mockMvc.perform(
                        delete("/groups/{groupId}/members/{memberId}", groupId, memberId)
                ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(NOT_GROUP_MEMBER_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(NOT_GROUP_MEMBER_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("회원이 운동 그룹을 탈퇴하는 메소드에서 JoinListNotFoundException 발싱시 적절한 응답을 반환하는지 테스트")
    void leaveGroupByMemberJoinListNotFoundExceptionTest() throws Exception {
        // given
        Long groupId = 1L;
        doThrow(JoinListNotFoundException.class).when(groupService).leaveGroupByMember(anyLong(), anyString());

        // when, then
        mockMvc.perform(
                        delete("/groups/{groupId}", groupId)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(JOIN_LIST_NOT_FOUND_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(JOIN_LIST_NOT_FOUND_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("운동 그룹 시작 메소드에서 NotExgroupHostException 발싱시 적절한 응답을 반환하는지 테스트")
    void startGroupNotExgroupHostExceptionTest() throws Exception {
        // given
        Long groupId = 1L;
        given(groupService.startGroup(anyLong(), anyString())).willThrow(NotGroupHostException.class);

        // when, then
        mockMvc.perform(
                        patch("/groups/{groupId}/initiation", groupId)
                ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(NOT_GROUP_HOST_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(NOT_GROUP_HOST_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("코드로 지인 그룹 가입 메소드에서 AlreadyJoinedGroupException 발생시 적절한 응답을 반환하는지 테스트")
    void joinFriendGroupAlreadyJoinedGroupExceptionTest() throws Exception {
        // given
        JoinFriendGroupRequest request = createJoinFriendGroupRequest();
        doThrow(AlreadyJoinedGroupException.class).when(groupService).joinFriendGroup(any(), anyString());

        // when, then
        mockMvc.perform(
                        post("/groups/join/code")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ALREADY_JOINED_GROUP_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(ALREADY_JOINED_GROUP_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("코드로 지인 그룹 가입 메소드에서 ExceedsKickOutLimitException 발생시 적절한 응답을 반환하는지 테스트")
    void joinFriendGroupExceedsKickOutLimitExceptionTest() throws Exception {
        // given
        JoinFriendGroupRequest request = createJoinFriendGroupRequest();
        doThrow(ExceedsKickOutLimitException.class).when(groupService).joinFriendGroup(any(), anyString());

        // when, then
        mockMvc.perform(
                        post("/groups/join/code")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(EXCEEDS_KICK_OUT_LIMIT_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(EXCEEDS_KICK_OUT_LIMIT_EXCEPTION.getMessage()));
    }
}
