package com.soma.snackexercise.domain.member;

import com.soma.snackexercise.domain.BaseEntity;
import com.soma.snackexercise.dto.member.request.MemberUpdateRequest;
import com.soma.snackexercise.util.constant.Gender;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String profileImage;

    private String nickname;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    private Integer birthYear;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String fcmToken;

    public void update(MemberUpdateRequest request) {
        this.profileImage = request.getProfileImage();
        this.name = request.getName();
    }

    public void updateFcmToken(String fcmToken){
        this.fcmToken = fcmToken;
    }

    public void deleteFcmToken(){
        this.fcmToken = null;
    }


    public void signupMemberInfo(String name, Gender gender, Integer birthYear) {
        this.name = name;
        this.gender = gender;
        this.birthYear = birthYear;
        this.role = Role.USER;
    }

    @Builder
    public Member(String email, String password, String profileImg, String nickname, String name, Role role, SocialType socialType, String socialId, Integer birthYear, Gender gender) {
        this.email = email;
        this.nickname = nickname;
        this.name = name;
        this.role = role;
        this.socialType = socialType;
        this.socialId = socialId;
        this.birthYear = birthYear;
        this.gender = gender;
        active();
    }
}
