package com.soma.snackexercise.factory.entity;

import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.member.Role;
import com.soma.snackexercise.domain.member.SocialType;
import com.soma.snackexercise.util.constant.Gender;

public class MemberFactory {
    public static Member createMember() {
        return new Member(1L,
                "test@naver.com",
                "1111",
                "profile.jpg",
                "nickname",
                "name",
                Role.USER,
                SocialType.KAKAO,
                "1111",
                1999,
                Gender.MALE,
                "token");
    }

    public static Member createMemberWithId(Long id) {
        return new Member(id,
                "test@naver.com",
                "1111",
                "profile.jpg",
                "nickname",
                "name",
                Role.USER,
                SocialType.KAKAO,
                "1111",
                1999,
                Gender.MALE,
                "token");
    }

    public static Member createMemberWithEmail(String email) {
        return new Member(1L,
                email,
                "1111",
                "profile.jpg",
                "nickname",
                "name",
                Role.USER,
                SocialType.KAKAO,
                "1111",
                1999,
                Gender.MALE,
                "token");
    }

    public static Member createMemberWithName(String name) {
        return new Member(1L,
                "test@naver.com",
                "1111",
                "profile.jpg",
                "nickname",
                name,
                Role.USER,
                SocialType.KAKAO,
                "1111",
                1999,
                Gender.MALE,
                "token");
    }

    public static Member createMemberWithSocialTypeAndSocialId(SocialType socialType, String socialId) {
        return new Member(1L,
                "test@naver.com",
                "1111",
                "profile.jpg",
                "nickname",
                "name",
                Role.USER,
                socialType,
                socialId,
                1999,
                Gender.MALE,
                "token");
    }
}
