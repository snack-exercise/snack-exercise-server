package com.soma.snackexercise.dto.member.response;

import com.soma.snackexercise.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateFcmTokenResponse {
    private Long id;
    private String nickname;
    private String fcmToken;

    public static MemberUpdateFcmTokenResponse toDto(Member member) {
        return new MemberUpdateFcmTokenResponse(member.getId(), member.getNickname(), member.getFcmToken());
    }
}
