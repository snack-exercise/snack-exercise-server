package com.soma.snackexercise.service.group;

import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.group.request.GroupCreateRequest;
import com.soma.snackexercise.dto.group.request.GroupUpdateRequest;
import com.soma.snackexercise.dto.group.request.JoinFriendGroupRequest;
import com.soma.snackexercise.dto.group.response.GroupCreateResponse;
import com.soma.snackexercise.dto.group.response.GroupResponse;
import com.soma.snackexercise.dto.member.JoinListMemberDto;
import com.soma.snackexercise.dto.member.response.GetOneGroupMemberResponse;
import com.soma.snackexercise.exception.group.*;
import com.soma.snackexercise.exception.joinlist.JoinListNotFoundException;
import com.soma.snackexercise.exception.member.MemberNotFoundException;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.soma.snackexercise.domain.joinlist.JoinType.HOST;
import static com.soma.snackexercise.domain.joinlist.JoinType.MEMBER;
import static com.soma.snackexercise.factory.dto.GroupCreateFactory.createGroupCreateRequest;
import static com.soma.snackexercise.factory.dto.GroupCreateFactory.createJoinFriendGroupRequest;
import static com.soma.snackexercise.factory.dto.GroupUpdateFactory.createGroupUpdateRequest;
import static com.soma.snackexercise.factory.entity.GroupFactory.createGroup;
import static com.soma.snackexercise.factory.entity.JoinListFactory.createJoinListForHost;
import static com.soma.snackexercise.factory.entity.JoinListFactory.createJoinListForMember;
import static com.soma.snackexercise.factory.entity.MemberFactory.createMember;
import static com.soma.snackexercise.util.constant.Status.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("GroupService 비즈니스 로직 테스트")
class GroupServiceTest {
    @InjectMocks
    private GroupService groupService;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private JoinListRepository joinListRepository;

    private String email = "test@naver.com";

    @Test
    @DisplayName("운동 그룹 생성 메소드 성공 테스트")
    void createTest() {
        //given
        GroupCreateRequest request = createGroupCreateRequest();
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.of(createMember()));
        given(groupRepository.existsByCodeAndStatus(anyString(), any())).willReturn(Boolean.FALSE);

        // when
        GroupCreateResponse response = groupService.create(request, email);

        // then
        assertThat(request.getName()).isEqualTo(response.getName());
    }

    @Test
    @DisplayName("운동 그룹 생성 메소드에서 그룹 생성할 회원 조회 예외 클래스 발생 테스트")
    void createExceptionByMemberNotFoundTest() {
        // given
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> groupService.create(createGroupCreateRequest(), email)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("운동 그룹 생성 메소드에서 그룹 코드 중복 검사 테스트")
    void createDuplicateGroupCodeTest() {
        // given
        String groupCode = "code";
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.of(createMember()));
        given(groupRepository.existsByCodeAndStatus(any(), any())).willReturn(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);

        // when
        groupService.create(createGroupCreateRequest(), email);

        // then
        verify(groupRepository, times(3)).existsByCodeAndStatus(any(), any());
    }

    @Test
    @DisplayName("운동 그룹 조회 메소드 성공 테스트")
    void readTest() {
        // given
        Group group = createGroup();
        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(group));

        // when
        GroupResponse response = groupService.read(1L);

        // then
        assertThat(response.getName()).isEqualTo(group.getName());
    }

    @Test
    @DisplayName("운동 그룹 조회 메소드에서 찾을 수 없을 때 예외 클래스 발생 테스트")
    void readExceptionByGroupNotFoundTest() {
        // given
        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> groupService.read(1L)).isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    @DisplayName("그룹에 속한 모든 멤버들을 조회하는 메소드 테스트")
    void getAllGroupMembersTest() {
        // given
        Member member = createMember();
        Member host = createMember();
        Group group = createGroup();

        given(memberRepository.findAllGroupMembers(anyLong())).willReturn(
                Arrays.asList(
                        new JoinListMemberDto(member, createJoinListForMember(member, group)),
                        new JoinListMemberDto(host, createJoinListForHost(host, group))
                ));

        // when
        List<GetOneGroupMemberResponse> response = groupService.getAllExgroupMembers(anyLong());

        // then
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.stream().map(GetOneGroupMemberResponse::getJoinType)).containsExactlyInAnyOrder(HOST, MEMBER);
        verify(memberRepository, times(1)).findAllGroupMembers(anyLong());
    }

    @Test
    @DisplayName("그룹 수정 메소드 성공 테스트")
    void updateTest() {
        // given
        GroupUpdateRequest request = createGroupUpdateRequest();
        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(createGroup()));
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createMember()));
        given(joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(any(), any(), any(), any())).willReturn(true);
        given(joinListRepository.countByGroupAndOutCountLessThanOneAndStatusEqualsActive(any())).willReturn(request.getMaxMemberNum());

        // when
        GroupResponse response = groupService.update(1L, email, request);

        // then
        assertNotNull(response);
        assertThat(response.getName()).isEqualTo(request.getName());
        verify(groupRepository, times(1)).findByIdAndStatus(anyLong(), any());
        verify(memberRepository, times(1)).findByEmailAndStatus(anyString(), any());
        verify(joinListRepository, times(1)).existsByGroupAndMemberAndJoinTypeAndStatus(any(), any(), any(), any());
        verify(joinListRepository, times(1)).countByGroupAndOutCountLessThanOneAndStatusEqualsActive(any());
    }

    @Test
    @DisplayName("그룹 수정 메소드에서 방장 권한이 아닌 멤버일 때 예외 클래스 발생 테스트")
    void updateExceptionByNotGroupHostExceptionTest() {
        // given
        GroupUpdateRequest request = createGroupUpdateRequest();
        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(createGroup()));
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createMember()));
        given(joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(any(), any(), any(), any())).willReturn(false);

        // when, then
        assertThatThrownBy(() -> groupService.update(1L, email, request)).isInstanceOf(NotGroupHostException.class);
    }

    @Test
    @DisplayName("그룹 수정 메소드에서 현재 그룹 인원수가 수정할 최대 인원 수보다 많을 때 발생하는 예외 클래스 발생 테스트")
    void updateExceptionByMaxMemberNumLessThanCurrentExceptionTest() {
        // given
        GroupUpdateRequest request = createGroupUpdateRequest();
        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(createGroup()));
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createMember()));
        given(joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(any(), any(), any(), any())).willReturn(true);
        given(joinListRepository.countByGroupAndOutCountLessThanOneAndStatusEqualsActive(any())).willReturn(request.getMaxMemberNum() + 1);

        // when, then
        assertThatThrownBy(() -> groupService.update(1L, email, request)).isInstanceOf(exceedGroupMaxMemberNumException.class);
    }

    @Test
    @DisplayName("방장이 회원 강퇴 메소드 성공 테스트")
    void deleteMemberByHostTest() {
        // given
        Member member = createMember();
        Group group = createGroup();
        JoinList joinList = createJoinListForMember(member, group);

        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(group));
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createMember()));
        given(memberRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(member));
        given(joinListRepository.findByGroupAndMemberAndStatus(any(), any(), any())).willReturn(Optional.ofNullable(joinList));
        given(joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(any(), any(), any(), any())).willReturn(true);

        // when
        groupService.deleteMemberByHost(1L, 1L, email);

        // then
        verify(groupRepository, times(1)).findByIdAndStatus(anyLong(), any());
        verify(memberRepository, times(1)).findByEmailAndStatus(anyString(), any());
        verify(memberRepository, times(1)).findByIdAndStatus(anyLong(), any());
        verify(joinListRepository, times(1)).findByGroupAndMemberAndStatus(any(), any(), any());
        verify(joinListRepository, times(2)).existsByGroupAndMemberAndJoinTypeAndStatus(any(), any(), any(), any());
    }

    @Test
    @DisplayName("방장이 회원 강퇴 메소드에서 현재 사용자가 방장 권한이 아닐 때 예외클래스 발생 테스트")
    void deleteMemberByHostTestExceptionByNotGroupHostExceptionTest() {
        // given
        Member member = createMember();
        Group group = createGroup();
        JoinList joinList = createJoinListForMember(member, group);

        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(group));
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createMember()));
        given(memberRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(member));
        given(joinListRepository.findByGroupAndMemberAndStatus(any(), any(), any())).willReturn(Optional.ofNullable(joinList));
        given(joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(any(), any(), any(), any())).willReturn(false);

        // when, then
        assertThatThrownBy(() -> groupService.deleteMemberByHost(1L, 1L, email)).isInstanceOf(NotGroupHostException.class);
    }

    @Test
    @DisplayName("방장이 회원 강퇴 메소드에서 타겟 사용자가 멤버 권한이 아닐 때 예외클래스 발생 테스트")
    void deleteMemberByHostTestExceptionByNotGroupMemberExceptionTest() {
        // given
        Member member = createMember();
        Group group = createGroup();
        JoinList joinList = createJoinListForMember(member, group);

        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(group));
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createMember()));
        given(memberRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(member));
        given(joinListRepository.findByGroupAndMemberAndStatus(any(), any(), any())).willReturn(Optional.ofNullable(joinList));
        // todo : org.mockito.exceptions.misusing.PotentialStubbingProblem
        given(joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(any(Group.class), any(Member.class), eq(HOST), eq(ACTIVE))).willReturn(true);
        given(joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(any(Group.class), any(Member.class), eq(MEMBER), eq(ACTIVE))).willReturn(false);

        // when, then
        assertThatThrownBy(() -> groupService.deleteMemberByHost(1L, 1L, email)).isInstanceOf(NotGroupMemberException.class);
    }

    @Test
    @DisplayName("회원이 그룹을 탈퇴하는 메소드 성공 테스트")
    void leaveGroupByMemberTest() {
        // given
        Member member = createMember();
        Group group = createGroup();
        JoinList joinList = createJoinListForMember(member, group);

        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(member));
        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(group));
        given(joinListRepository.findByGroupAndMemberAndStatus(any(), any(), any())).willReturn(Optional.ofNullable(joinList));
        given(joinListRepository.existsByGroupAndStatus(any(), any())).willReturn(true);

        // when
        groupService.leaveGroupByMember(1L, email);

        // then
        verify(memberRepository, times(1)).findByEmailAndStatus(anyString(), any());
        verify(groupRepository, times(1)).findByIdAndStatus(anyLong(), any());
        verify(joinListRepository, times(1)).findByGroupAndMemberAndStatus(any(), any(), any());
        verify(joinListRepository, times(1)).existsByGroupAndStatus(any(), any());
        verify(joinListRepository, times(0)).findFirstByGroupAndStatusOrderByCreatedAtAsc(any(), any());
    }

    @Test
    @DisplayName("회원이 그룹을 탈퇴하는 메소드에서 방장이 탈퇴했을 때 동작 검증 테스트")
    void leaveGroupByMemberByHost() {
        // given
        Member member = createMember();
        Group group = createGroup();
        JoinList joinList = createJoinListForHost(member, group);

        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(member));
        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(group));
        given(joinListRepository.findByGroupAndMemberAndStatus(any(), any(), any())).willReturn(Optional.ofNullable(joinList));
        given(joinListRepository.existsByGroupAndStatus(any(), any())).willReturn(true);
        given(joinListRepository.findFirstByGroupAndStatusOrderByCreatedAtAsc(any(), any())).willReturn(Optional.ofNullable(joinList));

        // when
        groupService.leaveGroupByMember(1L, email);

        // then
        verify(joinListRepository, times(1)).findFirstByGroupAndStatusOrderByCreatedAtAsc(any(), any());
    }

    @Test
    @DisplayName("회원이 그룹을 탈퇴하는 메소드에서 방장이 탈퇴했을 때 방장을 찾을 수 없을 때 예외 클래스 동작 테스트")
    void leaveGroupByMemberByHostExceptionByJoinListNotFoundException() {
        // given
        Member member = createMember();
        Group group = createGroup();
        JoinList joinList = createJoinListForHost(member, group);

        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(member));
        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(group));
        given(joinListRepository.findByGroupAndMemberAndStatus(any(), any(), any())).willReturn(Optional.ofNullable(joinList));
        given(joinListRepository.existsByGroupAndStatus(any(), any())).willReturn(true);
        given(joinListRepository.findFirstByGroupAndStatusOrderByCreatedAtAsc(any(), any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> groupService.leaveGroupByMember(1L, email)).isInstanceOf(JoinListNotFoundException.class);
    }

    @Test
    @DisplayName("방장이 그룹 시작하는 메소드 성공 테스트")
    void startGroupTest() {
        // given
        Member member = createMember();
        Group group = createGroup();

        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(member));
        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(group));
        given(joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(any(), any(), any(), any())).willReturn(true);

        // when
        GroupResponse response = groupService.startGroup(1L, email);

        // then
        verify(memberRepository, times(1)).findByEmailAndStatus(anyString(), any());
        verify(groupRepository, times(1)).findByIdAndStatus(anyLong(), any());
        verify(joinListRepository, times(1)).existsByGroupAndMemberAndJoinTypeAndStatus(any(), any(), any(), any());
        assertThat(response.getName()).isEqualTo(group.getName());
    }

    @Test
    @DisplayName("방장이 그룹 시작하는 메소드에서 현재 사용자가 방장 권한이 아닐 때 예외 클래스 발생 테스트")
    void startGroupByNotGroupHostExceptionTest() {
        // given
        Member member = createMember();
        Group group = createGroup();

        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(member));
        given(groupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(group));
        given(joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(any(), any(), any(), any())).willReturn(false);

        // when, then
        assertThatThrownBy(() -> groupService.startGroup(1L, email)).isInstanceOf(NotGroupHostException.class);
    }

    @Test
    @DisplayName("코드로 지인 그룹 가입 메소드 성공 테스트")
    void joinFriendGroupTest() {
        // given

        JoinFriendGroupRequest request = createJoinFriendGroupRequest();
        given(groupRepository.findByCodeAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createGroup()));
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createMember()));
        given(joinListRepository.existsByGroupAndMemberAndStatus(any(), any(), any())).willReturn(false);
        given(joinListRepository.existsByGroupAndMemberAndOutCountGreaterThanEqualAndStatus(any(), any(), any(), any())).willReturn(false);

        // when
        groupService.joinFriendGroup(request, email);

        // then
        verify(joinListRepository, times(1)).save(any(JoinList.class));
    }

    @Test
    @DisplayName("코드로 지인 그룹 가입 메소드 그룹에 이미 참여하고 있을 경우 예외 발생 테스트")
    void joinFriendGroupAlreadyJoinedGroupExceptionTest() {
        // given
        JoinFriendGroupRequest request = createJoinFriendGroupRequest();
        given(groupRepository.findByCodeAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createGroup()));
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createMember()));
        given(joinListRepository.existsByGroupAndMemberAndStatus(any(), any(), any())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> groupService.joinFriendGroup(request, email)).isInstanceOf(AlreadyJoinedGroupException.class);
    }

    @Test
    @DisplayName("코드로 지인 그룹 가입 메소드에서 그룹 강퇴 횟수가 2 이상인 경우 예외 발생 테스트")
    void joinFriendGroupExceedsKickOutLimitExceptionTest() {
        // given
        JoinFriendGroupRequest request = createJoinFriendGroupRequest();
        given(groupRepository.findByCodeAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createGroup()));
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.ofNullable(createMember()));
        given(joinListRepository.existsByGroupAndMemberAndStatus(any(), any(), any())).willReturn(false);
        given(joinListRepository.existsByGroupAndMemberAndOutCountGreaterThanEqualAndStatus(any(), any(), any(), any())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> groupService.joinFriendGroup(request, email)).isInstanceOf(ExceedsKickOutLimitException.class);
    }
}