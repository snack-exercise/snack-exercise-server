package com.soma.snackexercise.controller.exgroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soma.snackexercise.advice.ExceptionAdvice;
import com.soma.snackexercise.utils.TestUserArgumentResolver;
import com.soma.snackexercise.dto.exgroup.request.ExgroupCreateRequest;
import com.soma.snackexercise.dto.exgroup.request.ExgroupUpdateRequest;
import com.soma.snackexercise.exception.*;
import com.soma.snackexercise.service.exgroup.ExgroupService;
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
import static com.soma.snackexercise.factory.dto.ExgroupCreateFactory.createExgroupCreateRequest;
import static com.soma.snackexercise.factory.dto.ExgroupUpdateFactory.createExgroupUpdateRequest;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ExgroupControllerAdviceTest {
    @InjectMocks
    private ExgroupController exgroupController;

    @Mock
    private ExgroupService exgroupService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exgroupController)
                .setCustomArgumentResolvers(new TestUserArgumentResolver())
                .setControllerAdvice(new ExceptionAdvice())
                .build();
    }

    @Test
    @DisplayName("운동 그룹 생성 메소드에서 MemberNotFoundException 발생시 적절한 응답을 반환하는지 테스트")
    void createMemberNotFoundExceptionTest() throws Exception {
        // given
        ExgroupCreateRequest request = createExgroupCreateRequest();
        given(exgroupService.create(any(), anyString())).willThrow(MemberNotFoundException.class);

        // when, then
        mockMvc.perform(
                        post("/api/exgroups")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(MEMBER_NOT_FOUND_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("운동 그룹 조회 메소드에서 ExgroupNotFoundExceptionTest 발생시 적절한 응답을 반환하는지 테스트")
    void readExgroupNotFoundExceptionTest() throws Exception {
        // given
        Long groupId = 1L;
        given(exgroupService.read(anyLong())).willThrow(ExgroupNotFoundException.class);

        // when, then
        mockMvc.perform(
                        get("/api/exgroups/{groupId}", groupId)
                ).andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(EXGROUP_NOT_FOUND_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(EXGROUP_NOT_FOUND_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("운동 그룹 수정 메소드에서 NotExgroupHostException 발생시 적절한 응답을 반환하는지 테스트")
    void updateNotExgroupHostExceptionTest() throws Exception {
        // given
        Long groupId = 1L;
        ExgroupUpdateRequest request = createExgroupUpdateRequest();
        given(exgroupService.update(anyLong(), anyString(), any())).willThrow(NotExgroupHostException.class);

        // when, then
        mockMvc.perform(
                        patch("/api/exgroups/{groupId}", groupId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(NOT_EXGROUP_HOST_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(NOT_EXGROUP_HOST_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("운동 그룹에서 방장이 회원 강퇴 메소드에서 JoinListNotFoundException 발싱시 적절한 응답을 반환하는지 테스트")
    void deleteMemberByHostJoinListNotFoundExceptionTest() throws Exception {
        // given
        Long groupId = 1L;
        Long memberId = 1L;
        doThrow(JoinListNotFoundException.class).when(exgroupService).deleteMemberByHost(anyLong(), anyLong(), anyString());

        // when, then
        mockMvc.perform(
                        delete("/api/exgroups/{groupId}/members/{memberId}", groupId, memberId)
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
        doThrow(NotExgroupMemberException.class).when(exgroupService).deleteMemberByHost(anyLong(), anyLong(), anyString());

        // when, then
        mockMvc.perform(
                        delete("/api/exgroups/{groupId}/members/{memberId}", groupId, memberId)
                ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(NOT_EXGROUP_MEMBER_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(NOT_EXGROUP_MEMBER_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("회원이 운동 그룹을 탈퇴하는 메소드에서 JoinListNotFoundException 발싱시 적절한 응답을 반환하는지 테스트")
    void leaveGroupByMemberJoinListNotFoundExceptionTest () throws Exception {
        // given
        Long groupId = 1L;
        doThrow(JoinListNotFoundException.class).when(exgroupService).leaveGroupByMember(anyLong(), anyString());

        // when, then
        mockMvc.perform(
                        delete("/api/exgroups/{groupId}", groupId)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(JOIN_LIST_NOT_FOUND_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(JOIN_LIST_NOT_FOUND_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("운동 그룹 시작 메소드에서 NotExgroupHostException 발싱시 적절한 응답을 반환하는지 테스트")
    void startGroupNotExgroupHostExceptionTest () throws Exception {
        // given
        Long groupId = 1L;
        given(exgroupService.startGroup(anyLong(), anyString())).willThrow(NotExgroupHostException.class);

        // when, then
        mockMvc.perform(
                        patch("/api/exgroups/{groupId}/initiation", groupId)
                ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(NOT_EXGROUP_HOST_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.result.message").value(NOT_EXGROUP_HOST_EXCEPTION.getMessage()));
    }
}
