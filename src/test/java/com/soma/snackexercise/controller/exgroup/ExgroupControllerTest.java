package com.soma.snackexercise.controller.exgroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soma.snackexercise.config.TestUserArgumentResolver;
import com.soma.snackexercise.dto.exgroup.request.ExgroupCreateRequest;
import com.soma.snackexercise.dto.exgroup.request.ExgroupUpdateRequest;
import com.soma.snackexercise.dto.exgroup.response.ExgroupResponse;
import com.soma.snackexercise.dto.member.response.GetOneGroupMemberResponse;
import com.soma.snackexercise.service.exgroup.ExgroupService;
import com.soma.snackexercise.util.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;
import java.util.List;

import static com.soma.snackexercise.factory.dto.ExgroupCreateFactory.createExgroupCreateRequest;
import static com.soma.snackexercise.factory.dto.ExgroupReadFactory.createExgroupResponse;
import static com.soma.snackexercise.factory.dto.ExgroupReadFactory.createGetOneGroupMemberResponse;
import static com.soma.snackexercise.factory.dto.ExgroupUpdateFactory.createExgroupUpdateRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExgroupController 기능 동작 단위 테스트")
class ExgroupControllerTest {
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
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @DisplayName("운동 그룹 생성 메소드 동작 성공 테스트")
    void createGroupTest() throws Exception {
        // given
        ExgroupCreateRequest request = createExgroupCreateRequest();

        // when, then
        mockMvc.perform(
                post("/api/exgroups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isCreated());

        verify(exgroupService, times(1)).create(any(), anyString());
    }

    @Test
    @DisplayName("하나의 그룹 조회 메소드 동작 성공 테스트")
    void findGroupTest() throws Exception {
        // given
        Long groupId = 1L;

        // when, then
        mockMvc.perform(
                get("/api/exgroups/{groupId}", groupId)
        ).andExpect(status().isOk());

        verify(exgroupService, times(1)).read(groupId);
    }

    @Test
    @DisplayName("하나의 운동 그룹에 속한 모든 회원 조회 메소드 동작 성공 테스트")
    void getAllExgroupMembersTest() throws Exception {
        // given
        Long groupId = 1L;
        List<GetOneGroupMemberResponse> groupMembers = Arrays.asList(createGetOneGroupMemberResponse(), createGetOneGroupMemberResponse());
        when(exgroupService.getAllExgroupMembers(groupId)).thenReturn(groupMembers);

        // when, then
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/exgroups/{groupId}/members", groupId)
                ).andExpect(status().isOk())
                .andReturn();

        verify(exgroupService, times(1)).getAllExgroupMembers(groupId);

        // 반환된 값 검증
        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        String expectedResponseAsString = objectMapper.writeValueAsString(Response.success(groupMembers));
        assertEquals(actualResponseAsString, expectedResponseAsString);
    }

    @Test
    @DisplayName("하나의 운동 그룹 수정 메소드 동작 성공 테스트")
    void updateTest() throws Exception {
        // given
        Long groupId = 1L;
        ExgroupUpdateRequest request = createExgroupUpdateRequest();
        ExgroupResponse response = createExgroupResponse();
        when(exgroupService.update(anyLong(), anyString(), any())).thenReturn(response);

        // when, then
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/exgroups/{groupId}", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        verify(exgroupService, times(1)).update(anyLong(), anyString(), any());

        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        String expectedResponseAsString = objectMapper.writeValueAsString(Response.success(response));
        assertEquals(actualResponseAsString, expectedResponseAsString);
    }

    @Test
    @DisplayName("운동 그룹에서 방장이 회원 탈퇴 메소드 동작 성공 테스트")
    void deleteMemberByHostTest() throws Exception {
        // given
        Long groupId = 1L;
        Long memberId = 1L;

        // when, then
        mockMvc.perform(
                delete("/api/exgroups/{groupId}/members/{memberId}", groupId, memberId)
        ).andExpect(status().isOk());

        verify(exgroupService, times(1)).deleteMemberByHost(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("회원이 운동 그룹 탈퇴 메소드 동작 성공 테스트")
    void leaveGroupByMemberTest() throws Exception {
        // given
        Long groupId = 1L;

        // when, then
        mockMvc.perform(
                delete("/api/exgroups/{groupId}", groupId)
        ).andExpect(status().isOk());

        verify(exgroupService, times(1)).leaveGroupByMember(anyLong(), anyString());
    }

    @Test
    @DisplayName("운동 그룹 시작 메소드 동작 성공 테스트")
    void startGroupTest() throws Exception{
        // given
        Long groupId = 1L;
        ExgroupResponse response = createExgroupResponse();
        when(exgroupService.startGroup(anyLong(), anyString())).thenReturn(response);

        // when, then
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/exgroups/{groupId}/initiation", groupId)
        ).andExpect(status().isOk()).andReturn();

        verify(exgroupService, times(1)).startGroup(anyLong(), anyString());

        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        String expectedResponseAsString = objectMapper.writeValueAsString(Response.success(response));
        assertEquals(actualResponseAsString, expectedResponseAsString);
    }
}