package com.soma.snackexercise.service.group;

import com.soma.snackexercise.domain.group.CreateGroupCode;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.group.request.GroupCreateRequest;
import com.soma.snackexercise.dto.group.request.GroupUpdateRequest;
import com.soma.snackexercise.dto.group.request.JoinFriendGroupRequest;
import com.soma.snackexercise.dto.group.response.GroupCreateResponse;
import com.soma.snackexercise.dto.group.response.GroupResponse;
import com.soma.snackexercise.dto.group.response.JoinGroupResponse;
import com.soma.snackexercise.dto.member.JoinListMemberDto;
import com.soma.snackexercise.dto.member.response.GetOneGroupMemberResponse;
import com.soma.snackexercise.exception.*;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.util.constant.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// TODO : jakarata Transactional과 spring Transactional의 차이는 뭘까
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final JoinListRepository joinListRepository;


    @Transactional
    public GroupCreateResponse create(GroupCreateRequest groupCreateRequest, String email){

        // 1. 그룹 생성할 회원 조회
        Member member = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);

        // 2. 그룹 코드 생성
        String groupCode = CreateGroupCode.createGroupCode();
        Boolean isDuplicatedGroupCode = groupRepository.existsByCodeAndStatus(groupCode, Status.ACTIVE); // 코드 중복 여부

        // 3. 그룹 코드 중복 검사
        while(Boolean.TRUE.equals(isDuplicatedGroupCode)){ // 코드 중복 검사 불통과시 코드 재발급
            groupCode = CreateGroupCode.createGroupCode();
            isDuplicatedGroupCode = groupRepository.existsByCodeAndStatus(groupCode, Status.ACTIVE);
        }

        log.info("generate group code : {}", groupCode);

        // 4. 새로운 그룹 생성
        Group newGroup = Group.builder()
                .name(groupCreateRequest.getName())
                .emozi(groupCreateRequest.getEmozi())
                .color(groupCreateRequest.getColor())
                .description(groupCreateRequest.getDescription())
                .maxMemberNum(groupCreateRequest.getMaxMemberNum())
                .goalRelayNum(groupCreateRequest.getGoalRelayNum())
                .startTime(groupCreateRequest.getStartTime())
                .endTime(groupCreateRequest.getEndTime())
                .penalty(groupCreateRequest.getPenalty())
                .code(groupCode)
                .checkIntervalTime(groupCreateRequest.getCheckIntervalTime())
                .checkMaxNum(groupCreateRequest.getCheckMaxNum())
                .build();

        groupRepository.save(newGroup);

        // 5. 회원과 그룹 연결
        JoinList joinRequest = JoinList.builder()
                .member(member)
                .group(newGroup)
                .joinType(JoinType.HOST)
                .build();

        joinListRepository.save(joinRequest);
        return GroupCreateResponse.toDto(newGroup);
    }

    public GroupResponse read(Long groupId){
        Group group = groupRepository.findByIdAndStatus(groupId, Status.ACTIVE).orElseThrow(GroupNotFoundException::new);

        return GroupResponse.toDto(group);
    }

    public List<GetOneGroupMemberResponse> getAllExgroupMembers(Long groupId){
        List<JoinListMemberDto> allGroupMembers = memberRepository.findAllGroupMembers(groupId);

        return allGroupMembers.stream().map(data -> GetOneGroupMemberResponse.toDto(data.getMember(), data.getJoinList())).toList();
    }

    @Transactional
    public GroupResponse update(Long groupId, String email, GroupUpdateRequest request) {
        Group group = groupRepository.findByIdAndStatus(groupId, Status.ACTIVE).orElseThrow(GroupNotFoundException::new);
        Member member = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);

        // 1. 사용자가 해당 그룹의 방장인지 확인
        if (!joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(group, member, JoinType.HOST, Status.ACTIVE)) {
            throw new NotGroupHostException();
        }

        // 2. 그룹 최대 참여 인원 수 >= 현재 인원 수인지 판별
        group.updateMaxMemberNum(joinListRepository.countByGroupAndOutCountLessThanOneAndStatusEqualsActive(group), request.getMaxMemberNum());
        group.update(request);

        return GroupResponse.toDto(group);
    }

    // 방장이 회원 강퇴
    @Transactional
    public void deleteMemberByHost(Long groupId, Long memberId, String email) {
        Group group = groupRepository.findByIdAndStatus(groupId, Status.ACTIVE).orElseThrow(GroupNotFoundException::new);
        Member currentMember = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);
        Member targetMember = memberRepository.findByIdAndStatus(memberId, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);
        JoinList joinList = joinListRepository.findByGroupAndMemberAndStatus(group, targetMember, Status.ACTIVE).orElseThrow(JoinListNotFoundException::new);

        // 1. 사용자가 해당 그룹의 방장인지 확인
        if (!joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(group, currentMember, JoinType.HOST, Status.ACTIVE)) {
            throw new NotGroupHostException();
        }

        // 2. 삭제 타겟 회원이 해당 그룹의 멤버인지 확인
        if (!joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(group, targetMember, JoinType.MEMBER, Status.ACTIVE)) {
            throw new NotGroupMemberException();
        }

        // 3. joinList inActive로 변경
        joinList.inActive();

        // 4. joinList outCount + 1
        joinList.addOneOutCount();

        // TODO :  만약 미션 수행 중인 회원이 탈퇴당한다면 -> 다음 사람으로 로직 추가, 미션 상태 변경
        //if (exgroup.getCurrentDoingMemberId().equals(currentMember.getId())) {

        //}
    }

    // 회원이 그룹 탈퇴
    @Transactional
    public void leaveGroupByMember(Long groupId, String email) {
        Member member = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);
        Group group = groupRepository.findByIdAndStatus(groupId, Status.ACTIVE).orElseThrow(GroupNotFoundException::new); // TODO : ActiveExgroupNotFoundException로 이름 변경?
        JoinList joinList = joinListRepository.findByGroupAndMemberAndStatus(group, member, Status.ACTIVE).orElseThrow(JoinListNotFoundException::new);

        joinList.inActive();

        // 만약 그룹에 남은 사람이 없다면 그룹 폭파
        if (!joinListRepository.existsByGroupAndStatus(group, Status.ACTIVE)) {
            group.inActive();
            return;
        }

        // 만약 방장이 탈퇴한거면 방장을 누군가에게 위임하는 로직
        // 현재 그룹에 남은 멤버들 중 가장 오래된 멤버에게 방장 위임
        if (joinList.getJoinType().equals(JoinType.HOST)) {
            JoinList oldestMemberJoinList = joinListRepository.findFirstByGroupAndStatusOrderByCreatedAtAsc(group, Status.ACTIVE).orElseThrow(JoinListNotFoundException::new);
            oldestMemberJoinList.promoteToHost();
        }

        // TODO :  만약 미션 수행 중인 회원이 탈퇴당한다면 -> 다음 사람으로 로직 추가, 미션 상태 변경
        //if (exgroup.getCurrentDoingMemberId().equals(member.getId())) {

        //}
    }
    @Transactional
    public GroupResponse startGroup(Long groupId, String email){
        Member member = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);
        Group group = groupRepository.findByIdAndStatus(groupId, Status.ACTIVE).orElseThrow(GroupNotFoundException::new);

        // 1. 사용자가 해당 그룹의 방장인지 확인
        if (!joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(group, member, JoinType.HOST, Status.ACTIVE)) {
            throw new NotGroupHostException();

        }

        // 2. 그룹의 시작일자, 끝일자 기록
        group.updateStartDateAndEndDate();

        return GroupResponse.toDto(group);
    }

    @Transactional
    public void joinFriendGroup(JoinFriendGroupRequest request, String email) {
        Group group = groupRepository.findByCodeAndStatus(request.getCode(), Status.ACTIVE).orElseThrow(GroupNotFoundException::new);
        Member member = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);

        validateJoinGroup(group, member);

        joinListRepository.save(
                JoinList.builder()
                .member(member)
                .group(group)
                .joinType(JoinType.MEMBER).build());
    }

    private void validateJoinGroup(Group group, Member member) {
        final int KICK_OUT_LIMIT = 2;

        // 그룹에 이미 참여하고 있다면 예외 발생
        if (joinListRepository.existsByGroupAndMemberAndStatus(group, member, Status.ACTIVE)) {
            throw new AlreadyJoinedGroupException();
        }

        // 그룹 강퇴 횟수가 2이상이라면 예외 발생
        if (joinListRepository.existsByGroupAndMemberAndOutCountGreaterThanEqualAndStatus(group, member, KICK_OUT_LIMIT, Status.INACTIVE)) {
            throw new ExceedsKickOutLimitException();
        }
    }

    public List<JoinGroupResponse> readAllJoinGroups(String email) {
        Member member = memberRepository.findByEmailAndStatus(email, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);

        // 회원이 가입한 모든 그룹 조회
        List<JoinList> joinLists = joinListRepository.findAllActiveJoinGroupsByMember(member);

        // 그룹 ID, 그룹 명, 현재 미션 진행중인 회원의 ID 추출
        return joinLists.stream().map(joinList -> JoinGroupResponse.toDto(joinList.getGroup())).toList();
    }
}
