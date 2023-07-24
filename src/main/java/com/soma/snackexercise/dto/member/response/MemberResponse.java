package com.soma.snackexercise.dto.member.response;

import com.soma.snackexercise.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberResponse {
    private Long id;
    private String profileImage;
    private String name;

    public static MemberResponse toDto(Member member) {
        return new MemberResponse(member.getId(), member.getProfileImage(), member.getName());
    }
}
