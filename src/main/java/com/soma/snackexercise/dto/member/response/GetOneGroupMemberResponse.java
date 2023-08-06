package com.soma.snackexercise.dto.member.response;

import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.util.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetOneGroupMemberResponse {
    private String profileImg;

    private String nickname;

    private JoinType joinType;

    private Status status;

    public static GetOneGroupMemberResponse toDto(Member member, JoinList joinList) {
        return new GetOneGroupMemberResponse(
                member.getProfileImage(),
                member.getNickname(),
                joinList.getJoinType(),
                joinList.getStatus()
        );
    }

}
