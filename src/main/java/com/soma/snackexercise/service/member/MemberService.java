package com.soma.snackexercise.service.member;

import com.soma.snackexercise.dto.member.JoinListMemberDto;
import com.soma.snackexercise.dto.member.response.GetOneGroupMemberResponse;
import com.soma.snackexercise.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public List<GetOneGroupMemberResponse> getAllExgroupMembers(Long groupId){
        List<JoinListMemberDto> allGroupMembers = memberRepository.findAllGroupMembers(groupId);

        return allGroupMembers.stream().map(data -> new GetOneGroupMemberResponse(data.getMember().getProfileImage(), data.getMember().getNickname(), data.getJoinList().getJoinType(), data.getJoinList().getStatus())).toList();
    }


}
