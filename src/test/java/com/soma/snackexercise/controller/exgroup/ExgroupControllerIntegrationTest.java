package com.soma.snackexercise.controller.exgroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.exgroup.request.ExgroupCreateRequest;
import com.soma.snackexercise.dto.exgroup.request.ExgroupUpdateRequest;
import com.soma.snackexercise.exception.ExgroupNotFoundException;
import com.soma.snackexercise.exception.JoinListNotFoundException;
import com.soma.snackexercise.init.TestInitDB;
import com.soma.snackexercise.repository.exgroup.ExgroupRepository;
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

import static com.soma.snackexercise.factory.dto.ExgroupCreateFactory.createExgroupCreateRequest;
import static com.soma.snackexercise.factory.dto.ExgroupUpdateFactory.createExgroupUpdateRequest;
import static com.soma.snackexercise.factory.entity.ExgroupFactory.createExgroup;
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
@DisplayName("ExgroupController 통합 테스트")
public class ExgroupControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ExgroupRepository exgroupRepository;

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
        ExgroupCreateRequest request = createExgroupCreateRequest();
        int beforeSize = exgroupRepository.findAll().size();
        clear();

        // when, then
        mockMvc.perform(
                        post("/api/exgroups")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.data.name").value(request.getName()));

        int afterSize = exgroupRepository.findAll().size();
        assertThat(afterSize).isEqualTo(beforeSize + 1);
    }

    @Test
    @DisplayName("운동 그룹 조회 API 테스트")
    @WithMockUser(username = "test")
    void readTest() throws Exception {
        // given
        Exgroup exgroup = exgroupRepository.save(createExgroup());
        clear();

        // when, then
        mockMvc.perform(
                        get("/api/exgroups/{groupId}", exgroup.getId())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.data.id").value(exgroup.getId()))
                .andExpect(jsonPath("$.result.data.name").value(exgroup.getName()));
    }

    @Test
    @DisplayName("운동 그룹에 속한 모든 회원 조회 API 테스트")
    @WithMockUser(username = "test")
    void readAllMembersTest() throws Exception{
        // given
        Exgroup exgroup = exgroupRepository.save(createExgroup());
        Member member1 = memberRepository.save(createMember());
        Member member2 = memberRepository.save(createMember());
        joinListRepository.save(createJoinListForHost(member1, exgroup));
        joinListRepository.save(createJoinListForMember(member2, exgroup));
        clear();

        // when, then
        mockMvc.perform(
                        get("/api/exgroups/{groupId}/members", exgroup.getId())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data", hasSize(2)));
    }

    @Test
    @DisplayName("운동 그룹 수정 API 테스트")
    @WithMockUser(username = "test")
    void updateTest() throws Exception {
        // given
        Exgroup exgroup = exgroupRepository.save(createExgroup());
        joinListRepository.save(createJoinListForHost(member, exgroup));
        ExgroupUpdateRequest request = createExgroupUpdateRequest();
        clear();

        // when, then
        mockMvc.perform(
                        patch("/api/exgroups/{groupId}", exgroup.getId())
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
        Exgroup exgroup = exgroupRepository.save(createExgroup());
        Member targetMember = memberRepository.save(createMember());
        JoinList hostJoinList = joinListRepository.save(createJoinListForHost(member, exgroup));
        JoinList targetMemberJoinList = joinListRepository.save(createJoinListForMember(targetMember, exgroup));
        clear();

        // when, then
        mockMvc.perform(
                        delete("/api/exgroups/{groupId}/members/{memberId}", exgroup.getId(), targetMember.getId())
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
        Exgroup exgroup = exgroupRepository.save(createExgroup());
        JoinList joinList = joinListRepository.save(createJoinListForMember(member, exgroup));
        clear();

        // when, then
        mockMvc.perform(
                        delete("/api/exgroups/{groupId}", exgroup.getId())
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
        Exgroup exgroup = exgroupRepository.save(createExgroup());
        joinListRepository.save(createJoinListForHost(member, exgroup));
        clear();

        // when, then
        mockMvc.perform(
                        patch("/api/exgroups/{groupId}/initiation", exgroup.getId())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.data.name").value(exgroup.getName()));

        assertNull(exgroup.getStartDate());
        Exgroup updatedExgroup = exgroupRepository.findByIdAndStatus(exgroup.getId(), Status.ACTIVE).orElseThrow(ExgroupNotFoundException::new);
        assertNotNull(updatedExgroup.getStartDate());
        assertNotNull(updatedExgroup.getEndDate());
    }

    void clear() {
        entityManager.flush();
        entityManager.clear();
    }
}
