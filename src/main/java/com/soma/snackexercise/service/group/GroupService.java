package com.soma.snackexercise.service.group;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.group.CreateGroupCode;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.group.request.GroupCreateRequest;
import com.soma.snackexercise.dto.group.request.GroupUpdateRequest;
import com.soma.snackexercise.dto.group.request.JoinFriendGroupRequest;
import com.soma.snackexercise.dto.group.response.*;
import com.soma.snackexercise.dto.member.JoinListMemberDto;
import com.soma.snackexercise.dto.member.response.GetOneGroupMemberResponse;
import com.soma.snackexercise.exception.group.*;
import com.soma.snackexercise.exception.joinlist.JoinListNotFoundException;
import com.soma.snackexercise.exception.member.MemberNotFoundException;
import com.soma.snackexercise.repository.exercise.ExerciseRepository;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.service.mission.MissionUtil;
import com.soma.snackexercise.service.notification.FirebaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static com.soma.snackexercise.domain.notification.NotificationMessage.GROUP_START;
import static com.soma.snackexercise.util.constant.Status.ACTIVE;
import static com.soma.snackexercise.util.constant.Status.INACTIVE;

// TODO : jakarata Transactional과 spring Transactional의 차이는 뭘까
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final JoinListRepository joinListRepository;
    private final ExerciseRepository exerciseRepository;
    private final MissionUtil missionUtil;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @Transactional
    public GroupCreateResponse create(GroupCreateRequest groupCreateRequest, String email){

        // 1. 그룹 생성할 회원 조회
        Member member = memberRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(MemberNotFoundException::new);

        // 2. 그룹 코드 생성
        String groupCode = CreateGroupCode.createGroupCode();
        Boolean isDuplicatedGroupCode = groupRepository.existsByCodeAndStatus(groupCode, ACTIVE); // 코드 중복 여부

        // 3. 그룹 코드 중복 검사
        while(Boolean.TRUE.equals(isDuplicatedGroupCode)){ // 코드 중복 검사 불통과시 코드 재발급
            groupCode = CreateGroupCode.createGroupCode();
            isDuplicatedGroupCode = groupRepository.existsByCodeAndStatus(groupCode, ACTIVE);
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
                .existDays(groupCreateRequest.getExistDays())
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

    public GroupResponseIncludeHost read(Long groupId){
        JoinList joinList = joinListRepository.findHostJoinListByGroupIdAndStatus(groupId, ACTIVE).orElseThrow(GroupNotFoundException::new);

        return GroupResponseIncludeHost.toDto(joinList.getGroup(), joinList.getMember().getId());
    }

    public List<GetOneGroupMemberResponse> getAllExgroupMembers(Long groupId){
        List<JoinListMemberDto> allGroupMembers = memberRepository.findAllGroupMembers(groupId);

        return allGroupMembers.stream().map(data -> GetOneGroupMemberResponse.toDto(data.getMember(), data.getJoinList())).toList();
    }

    @Transactional
    public GroupResponse update(Long groupId, String email, GroupUpdateRequest request) {
        Group group = groupRepository.findByIdAndStatus(groupId, ACTIVE).orElseThrow(GroupNotFoundException::new);
        Member member = memberRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(MemberNotFoundException::new);

        // 1. 사용자가 해당 그룹의 방장인지 확인
        if (!joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(group, member, JoinType.HOST, ACTIVE)) {
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
        Group group = groupRepository.findByIdAndStatus(groupId, ACTIVE).orElseThrow(GroupNotFoundException::new);
        Member currentMember = memberRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(MemberNotFoundException::new);
        Member targetMember = memberRepository.findByIdAndStatus(memberId, ACTIVE).orElseThrow(MemberNotFoundException::new);
        JoinList joinList = joinListRepository.findByGroupAndMemberAndStatus(group, targetMember, ACTIVE).orElseThrow(JoinListNotFoundException::new);

        // 1. 사용자가 해당 그룹의 방장인지 확인
        if (!joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(group, currentMember, JoinType.HOST, ACTIVE)) {
            throw new NotGroupHostException();
        }

        // 2. 삭제 타겟 회원이 해당 그룹의 멤버인지 확인
        if (!joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(group, targetMember, JoinType.MEMBER, ACTIVE)) {
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
        Member member = memberRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(MemberNotFoundException::new);
        Group group = groupRepository.findByIdAndStatus(groupId, ACTIVE).orElseThrow(GroupNotFoundException::new); // TODO : ActiveExgroupNotFoundException로 이름 변경?
        JoinList joinList = joinListRepository.findByGroupAndMemberAndStatus(group, member, ACTIVE).orElseThrow(JoinListNotFoundException::new);

        joinList.inActive();

        // 만약 그룹에 남은 사람이 없다면 그룹 폭파
        if (!joinListRepository.existsByGroupAndStatus(group, ACTIVE)) {
            group.inActive();
            return;
        }

        // 만약 방장이 탈퇴한거면 방장을 누군가에게 위임하는 로직
        // 현재 그룹에 남은 멤버들 중 가장 오래된 멤버에게 방장 위임
        if (joinList.getJoinType().equals(JoinType.HOST)) {
            JoinList oldestMemberJoinList = joinListRepository.findFirstByGroupAndStatusOrderByCreatedAtAsc(group, ACTIVE).orElseThrow(JoinListNotFoundException::new);
            oldestMemberJoinList.promoteToHost();
        }

        // TODO :  만약 미션 수행 중인 회원이 탈퇴당한다면 -> 다음 사람으로 로직 추가, 미션 상태 변경
        //if (exgroup.getCurrentDoingMemberId().equals(member.getId())) {

        //}
    }
    @Transactional
    public GroupResponse startGroup(Long groupId, String email){
        Member member = memberRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(MemberNotFoundException::new);
        Group group = groupRepository.findByIdAndStatus(groupId, ACTIVE).orElseThrow(GroupNotFoundException::new);

        // 1. 사용자가 해당 그룹의 방장인지 확인
        if (!joinListRepository.existsByGroupAndMemberAndJoinTypeAndStatus(group, member, JoinType.HOST, ACTIVE)) {
            throw new NotGroupHostException();

        }

        // 2. 그룹의 시작일자, 끝일자 기록
        group.updateStartDateAndEndDate();

        // 3. 현재 시각이 그룹의 운동 시간 범위 내라면, 회원 1명에게 운동 할당
        LocalTime now = LocalTime.now();
        List<Exercise> exerciseList = exerciseRepository.findAll();

        if(now.isAfter(group.getStartTime()) && now.isBefore(group.getEndTime()) && group.getCurrentDoingMemberId() == null){ // 그룹 운동 시간 안이고, 그룹에 미션 할당된 사람이 없는 경우
            log.info("============= 그룹 시작, [그룹명] : {} =============", group.getName());
            Member targetMember = missionUtil.getNextMissionMember(group);
            missionUtil.allocateMission(targetMember, group, exerciseList);
        }

        // 4. 그룹원 모두에게 그룹 시작 푸시알림 전송
        List<String> tokenList = joinListRepository.findByGroupAndStatus(group, ACTIVE).stream().filter(joinList -> joinList.getMember().getFcmToken() != null).map(joinList -> joinList.getMember().getFcmToken()).toList();
        if (!tokenList.isEmpty()) {
            System.out.println("tokenList = " + tokenList);
            // tokenList로 알림 보내기
            firebaseCloudMessageService.sendByTokenList(tokenList, GROUP_START.getTitleWithGroupName(group.getName()), GROUP_START.getBody());
        }

        return GroupResponse.toDto(group);
    }

    @Transactional
    public void joinFriendGroup(JoinFriendGroupRequest request, String email) {
        Group group = groupRepository.findByCodeAndStatus(request.getCode(), ACTIVE).orElseThrow(GroupNotFoundException::new);
        Member member = memberRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(MemberNotFoundException::new);

        validateJoinGroup(group, member);

        // 이미 시작한 그룹에 중간에 참여하는 경우, 그룹원 중 가장 적은 수행횟수만큼 수행했다고 간주한다.
        if(group.isStarted()){
            JoinList joinList = JoinList.builder()
                    .member(member)
                    .group(group)
                    .joinType(JoinType.MEMBER)
                    .build();

            joinList.updateExecutedMissionCount(joinListRepository.findMinExecutedMissionCountByGroupAndStatus(group, ACTIVE));
            joinListRepository.save(joinList);
            return;
        }

        joinListRepository.save(
                JoinList.builder()
                .member(member)
                .group(group)
                .joinType(JoinType.MEMBER).build());
    }

    private void validateJoinGroup(Group group, Member member) {
        final int KICK_OUT_LIMIT = 2;

        // 그룹에 이미 참여하고 있다면 예외 발생
        if (joinListRepository.existsByGroupAndMemberAndStatus(group, member, ACTIVE)) {
            throw new AlreadyJoinedGroupException();
        }

        // 그룹 강퇴 횟수가 2이상이라면 예외 발생
        if (joinListRepository.existsByGroupAndMemberAndOutCountGreaterThanEqualAndStatus(group, member, KICK_OUT_LIMIT, INACTIVE)) {
            throw new ExceedsKickOutLimitException();
        }
    }

    public List<JoinGroupResponse> readAllJoinGroups(String email) {
        Member member = memberRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(MemberNotFoundException::new);

        // 회원이 가입한 모든 그룹 조회
        List<JoinList> joinLists = joinListRepository.findAllActiveJoinGroupsByMember(member);

        // 그룹 ID, 그룹 명, 현재 미션 진행중인 회원의 ID 추출
        return joinLists.stream().map(joinList -> JoinGroupResponse.toDto(joinList.getGroup())).toList();
    }

    public List<FinishedGroupResponse> readAllFinishedJoinGroups(String email) {
        Member member = memberRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(MemberNotFoundException::new);

        // 회원이 가입했지만 종료한 모든 그룹
        List<JoinList> joinLists = joinListRepository.findAllInactiveJoinGroupsByMember(member);
        return joinLists.stream().map(joinList -> FinishedGroupResponse.toDto(
                joinList.getGroup(),
                joinListRepository.findMaxExecutedMissionCountByGroupAndStatus(joinList.getGroup(), INACTIVE))).toList();// 그룹이 현재 완료한 릴레이 횟수

    }
}
