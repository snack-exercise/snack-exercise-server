package com.soma.snackexercise.controller.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.group.request.GroupCreateRequest;
import com.soma.snackexercise.dto.group.request.GroupUpdateRequest;
import com.soma.snackexercise.exception.GroupNotFoundException;
import com.soma.snackexercise.exception.JoinListNotFoundException;
import com.soma.snackexercise.init.TestInitDB;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.util.constant.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.soma.snackexercise.factory.dto.GroupCreateFactory.createGroupCreateRequest;
import static com.soma.snackexercise.factory.dto.GroupUpdateFactory.createGroupUpdateRequest;
import static com.soma.snackexercise.factory.entity.GroupFactory.createExgroup;
import static com.soma.snackexercise.factory.entity.JoinListFactory.createJoinListForHost;
import static com.soma.snackexercise.factory.entity.JoinListFactory.createJoinListForMember;
import static com.soma.snackexercise.factory.entity.MemberFactory.createMember;
import static com.soma.snackexercise.factory.entity.MemberFactory.createMemberWithEmail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("GroupController 통합 테스트")
public class GroupControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JoinListRepository joinListRepository;

    @Autowired
    private TestInitDB initDB;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Member member;

    private static String email = "test";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        // security 인증 정보를 제공하기 위한 유저 정보 저장
        member = memberRepository.save(createMemberWithEmail(email));
    }

    @Test
    @DisplayName("운동 그룹 생성 API 테스트")
    @WithMockUser(username = "test")
    void createTest() throws Exception {
        // given
        GroupCreateRequest request = createGroupCreateRequest();
        int beforeSize = groupRepository.findAll().size();
        clear();

        // when, then
        mockMvc.perform(
                        post("/api/groups")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.data.name").value(request.getName()));

        int afterSize = groupRepository.findAll().size();
        assertThat(afterSize).isEqualTo(beforeSize + 1);
    }

    @Test
    @DisplayName("운동 그룹 조회 API 테스트")
    @WithMockUser(username = "test")
    void readTest() throws Exception {
        // given
        Group group = groupRepository.save(createExgroup());
        clear();

        // when, then
        mockMvc.perform(
                        get("/api/groups/{groupId}", group.getId())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.data.id").value(group.getId()))
                .andExpect(jsonPath("$.result.data.name").value(group.getName()));
    }

    @Test
    @DisplayName("운동 그룹에 속한 모든 회원 조회 API 테스트")
    @WithMockUser(username = "test")
    void readAllMembersTest() throws Exception{
        // given
        Group group = groupRepository.save(createExgroup());
        Member member1 = memberRepository.save(createMember());
        Member member2 = memberRepository.save(createMember());
        joinListRepository.save(createJoinListForHost(member1, group));
        joinListRepository.save(createJoinListForMember(member2, group));
        clear();

        // when, then
        mockMvc.perform(
                        get("/api/groups/{groupId}/members", group.getId())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data", hasSize(2)));
    }

    @Test
    @DisplayName("운동 그룹 수정 API 테스트")
    @WithMockUser(username = "test")
    void updateTest() throws Exception {
        // given
        Group group = groupRepository.save(createExgroup());
        joinListRepository.save(createJoinListForHost(member, group));
        GroupUpdateRequest request = createGroupUpdateRequest();
        clear();

        // when, then
        mockMvc.perform(
                        patch("/api/groups/{groupId}", group.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.name").value(request.getName()));
    }

    @Test
    @DisplayName("운동 그룹에서 방장이 회원 탈퇴시키는 API 테스트")
    @WithMockUser(username = "test")
    void deleteMemberByHostTest() throws Exception {
        // given
        Group group = groupRepository.save(createExgroup());
        Member targetMember = memberRepository.save(createMember());
        JoinList hostJoinList = joinListRepository.save(createJoinListForHost(member, group));
        JoinList targetMemberJoinList = joinListRepository.save(createJoinListForMember(targetMember, group));
        clear();

        // when, then
        mockMvc.perform(
                        delete("/api/groups/{groupId}/members/{memberId}", group.getId(), targetMember.getId())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        JoinList updatedTargetMemberJoinList = joinListRepository.findById(targetMemberJoinList.getId()).orElseThrow(JoinListNotFoundException::new);
        assertThat(hostJoinList.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(updatedTargetMemberJoinList.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("회원이 운동 그룹을 탈퇴하는 API 테스트")
    @WithMockUser(username = "test")
    void leaveGroupByMemberTest() throws Exception {
        // given
        Group group = groupRepository.save(createExgroup());
        JoinList joinList = joinListRepository.save(createJoinListForMember(member, group));
        clear();

        // when, then
        mockMvc.perform(
                        delete("/api/groups/{groupId}", group.getId())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        JoinList updatedJoinList = joinListRepository.findById(joinList.getId()).orElseThrow(JoinListNotFoundException::new);
        assertThat(updatedJoinList.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("방장이 운동 그룹 시작하는 API 테스트")
    @WithMockUser(username = "test")
    void startGroupTest() throws Exception {
        // given
        Group group = groupRepository.save(createExgroup());
        joinListRepository.save(createJoinListForHost(member, group));
        clear();

        // when, then
        mockMvc.perform(
                        patch("/api/groups/{groupId}/initiation", group.getId())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.data.name").value(group.getName()));

        assertNull(group.getStartDate());
        Group updatedGroup = groupRepository.findByIdAndStatus(group.getId(), Status.ACTIVE).orElseThrow(GroupNotFoundException::new);
        assertNotNull(updatedGroup.getStartDate());
        assertNotNull(updatedGroup.getEndDate());
    }

    void clear() {
        entityManager.flush();
        entityManager.clear();
    }
}
