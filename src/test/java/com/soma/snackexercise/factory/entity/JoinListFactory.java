package com.soma.snackexercise.factory.entity;

import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.domain.member.Member;

public class JoinListFactory {
    public static JoinList createJoinListForMember(Member member, Group group) {
        return JoinList.builder()
                .member(member)
                .group(group)
                .joinType(JoinType.MEMBER)
                .build();
    }

    public static JoinList createJoinListForHost(Member member, Group group) {
        return JoinList.builder()
                .member(member)
                .group(group)
                .joinType(JoinType.HOST)
                .build();
    }
}
