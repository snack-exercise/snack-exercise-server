package com.soma.snackexercise.dto.member;

import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class JoinListMemberDto {
    private Member member;
    private JoinList joinList;
}
