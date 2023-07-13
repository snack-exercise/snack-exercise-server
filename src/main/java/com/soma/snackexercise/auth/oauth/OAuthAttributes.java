package com.soma.snackexercise.auth.oauth;

import com.soma.snackexercise.auth.oauth.userinfo.KakaoOAuth2UserInfo;
import com.soma.snackexercise.auth.oauth.userinfo.OAuth2UserInfo;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.member.Role;
import com.soma.snackexercise.domain.member.SocialType;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

/*
각 소셜에서 받아오는 데이터가 다르므로
소셜별로 데이터를 받는 데이터를 분기 처리하는 DTO 클래스
 */

@Getter
public class OAuthAttributes {
    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private OAuth2UserInfo oAuth2UserInfo; // 소셜  타입별 로그인 유저 정보

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    public static OAuthAttributes of(SocialType socialType, String userNameAttributeName, Map<String, Object> attributes) {
        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public Member toEntity(SocialType socialType, OAuth2UserInfo oAuth2UserInfo) {
        return Member.builder()
                .socialType(socialType)
                .socialId(oAuth2UserInfo.getId())
                .email(UUID.randomUUID() + "@socialUser.com")
                .nickname(oAuth2UserInfo.getNickname())
                .role(Role.GUEST)
                .build();
    }
}
