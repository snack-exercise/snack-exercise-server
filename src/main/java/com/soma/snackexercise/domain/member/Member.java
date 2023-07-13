package com.soma.snackexercise.domain.member;

import com.soma.snackexercise.domain.BaseTimeEntity;
import com.soma.snackexercise.util.constant.Gender;
import com.soma.snackexercise.util.constant.Status;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String profileImg;

    private String nickname;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    private Integer birthYear;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public void inActive() {
        this.status = Status.INACTIVE;
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
        this.status = Status.ACTIVE;
    }
}
