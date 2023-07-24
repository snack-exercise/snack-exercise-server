package com.soma.snackexercise.service.member;

import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.member.JoinListMemberDto;
import com.soma.snackexercise.dto.member.request.MemberUpdateRequest;
import com.soma.snackexercise.dto.member.response.GetOneGroupMemberResponse;
import com.soma.snackexercise.dto.member.response.MemberResponse;
import com.soma.snackexercise.exception.MemberNameAlreadyExistsException;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.util.constant.Status;
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

    public MemberResponse update(Long id, MemberUpdateRequest request) {
        Member member = memberRepository.findByIdAndStatus(id, Status.ACTIVE).orElseThrow(MemberNotFoundException::new);

        if (!member.getName().equals(request.getName())) {
            validateDuplicateName(request.getName());
        }

        member.update(request);
        return MemberResponse.toDto(member);
    }

    private void validateDuplicateName(String name) {
        if (memberRepository.existsByName(name)) {
            throw new MemberNameAlreadyExistsException(name);
        }
    }
}
