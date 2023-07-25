package com.soma.snackexercise.factory.entity;

import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.member.Role;
import com.soma.snackexercise.domain.member.SocialType;
import com.soma.snackexercise.util.constant.Gender;

public class MemberFactory {
    public static Member createMember() {
        return Member.builder()
                .email("test@naver.com")
                .nickname("nickname")
                .name("name")
                .role(Role.USER)
                .socialType(SocialType.KAKAO)
                .socialId("1111")
                .password("1111")
                .profileImg("profile.jpg")
                .gender(Gender.MALE)
                .birthYear(1999)
                .build();
    }

    public static Member createMember(String email) {
        return Member.builder()
                .email(email)
                .nickname("nickname")
                .name("name")
                .role(Role.USER)
                .socialType(SocialType.KAKAO)
                .socialId("1111")
                .password("1111")
                .profileImg("profile.jpg")
                .gender(Gender.MALE)
                .birthYear(1999)
                .build();
    }
}
