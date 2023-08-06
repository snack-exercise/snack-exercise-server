package com.soma.snackexercise.auth.oauth.userinfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo{
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getNickname() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("response");
        Map<String, Object> profile = (Map<String, Object>) attributes.get("profile");

        if (account == null || profile == null) {
            return null;
        }

        return (String) profile.get("nickname");
    }
}
