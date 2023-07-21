package com.soma.snackexercise.dto.member.response;

import com.soma.snackexercise.domain.joinlist.JoinType;
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

}
