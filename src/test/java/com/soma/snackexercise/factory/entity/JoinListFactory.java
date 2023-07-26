package com.soma.snackexercise.factory.entity;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.domain.member.Member;

public class JoinListFactory {
    public static JoinList createJoinListForMember(Member member, Exgroup exgroup) {
        return JoinList.builder()
                .member(member)
                .exgroup(exgroup)
                .joinType(JoinType.MEMBER)
                .build();
    }

    public static JoinList createJoinListForHost(Member member, Exgroup exgroup) {
        return JoinList.builder()
                .member(member)
                .exgroup(exgroup)
                .joinType(JoinType.HOST)
                .build();
    }
}
