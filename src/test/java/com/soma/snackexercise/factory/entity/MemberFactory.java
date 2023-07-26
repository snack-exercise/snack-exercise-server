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

    public static Member createMemberWithEmail(String email) {
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

    public static Member createMemberWithName(String name) {
        return Member.builder()
                .email("test@naver.com")
                .nickname("nickname")
                .name(name)
                .role(Role.USER)
                .socialType(SocialType.KAKAO)
                .socialId("1111")
                .password("1111")
                .profileImg("profile.jpg")
                .gender(Gender.MALE)
                .birthYear(1999)
                .build();
    }

    public static Member createMemberWithSocialTypeAndSocialId(SocialType socialType, String socialId) {
        return Member.builder()
                .email("test@naver.com")
                .nickname("nickname")
                .name("name")
                .role(Role.USER)
                .socialType(socialType)
                .socialId(socialId)
                .password("1111")
                .profileImg("profile.jpg")
                .gender(Gender.MALE)
                .birthYear(1999)
                .build();
    }
}
