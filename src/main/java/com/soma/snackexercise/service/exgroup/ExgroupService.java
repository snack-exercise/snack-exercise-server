package com.soma.snackexercise.service.exgroup;

import com.soma.snackexercise.domain.exgroup.CreateExgroupCode;
import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.exgroup.response.GetOneExgroupResponse;
import com.soma.snackexercise.dto.exgroup.request.PostCreateExgroupRequest;
import com.soma.snackexercise.dto.exgroup.response.PostCreateExgroupResponse;
import com.soma.snackexercise.exception.ExgroupNotFoundException;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.repository.exgroup.ExgroupRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExgroupService {
    private final ExgroupRepository exgroupRepository;
    private final MemberRepository memberRepository;
    private final JoinListRepository joinListRepository;


    @Transactional
    public PostCreateExgroupResponse createGroup(PostCreateExgroupRequest groupCreateRequest, String email){

        // 1. 그룹 생성할 회원 조회
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        // 2. 그룹 코드 생성
        String groupCode = CreateExgroupCode.createGroupCode();
        Boolean flag = exgroupRepository.existsByCode(groupCode); // 코드 중복 여부

        // 3. 그룹 코드 중복 검사
        while(Boolean.TRUE.equals(flag)){ // 코드 중복 검사 불통과시 코드 재발급
            groupCode = CreateExgroupCode.createGroupCode();
            flag = exgroupRepository.existsByCode(groupCode);
        }

        log.info("generate group code : {}", groupCode);

        // 4. 새로운 그룹 생성
        Exgroup newGroup = Exgroup.builder()
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
                .missionIntervalTime(groupCreateRequest.getMissionIntervalTime())
                .checkIntervalTime(groupCreateRequest.getCheckIntervalTime())
                .checkMaxNum(groupCreateRequest.getCheckMaxNum())
                .startDate(groupCreateRequest.getStartDate())
                .endDate(groupCreateRequest.getEndDate())
                .build();

        exgroupRepository.save(newGroup);

        // 5. 회원이 그룹 연결
        JoinList joinRequest = JoinList.builder()
                .member(member)
                .exgroup(newGroup)
                .joinType(JoinType.HOST)
                .build();

        joinListRepository.save(joinRequest);
        return new PostCreateExgroupResponse(newGroup.getId(), newGroup.getName());
    }

    public GetOneExgroupResponse findGroup(Long groupId){

        Exgroup exgroup = exgroupRepository.findById(groupId).orElseThrow(ExgroupNotFoundException::new);

        return GetOneExgroupResponse.builder()
                .name(exgroup.getName())
                .emozi(exgroup.getEmozi())
                .color(exgroup.getColor())
                .description(exgroup.getDescription())
                .maxMemberNum(exgroup.getMaxMemberNum())
                .goalRelayNum(exgroup.getGoalRelayNum())
                .startTime(exgroup.getStartTime())
                .endTime(exgroup.getEndTime())
                .penalty(exgroup.getPenalty())
                .code(exgroup.getCode())
                .missionIntervalTime(exgroup.getMissionIntervalTime())
                .checkIntervalTime(exgroup.getCheckIntervalTime())
                .checkMaxNum(exgroup.getCheckMaxNum())
                .startDate(exgroup.getStartDate())
                .endDate(exgroup.getEndDate())
                .build();
    }
}
