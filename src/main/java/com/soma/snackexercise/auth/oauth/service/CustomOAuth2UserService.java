package com.soma.snackexercise.auth.oauth.service;

import com.soma.snackexercise.auth.oauth.CustomOAuth2User;
import com.soma.snackexercise.auth.oauth.OAuthAttributes;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.member.SocialType;
import com.soma.snackexercise.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static com.soma.snackexercise.domain.member.SocialType.KAKAO;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    /*
    서드파티에 사용자 정보를 요청할 수 있는 access token을 얻고나서 실행된다.
    이 때 access token과 같은 정보들이 oAuth2UserRequest 파라미터에 들어있다.
     */

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        /*
        DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
        사용자 정보를 얻은 후, DefaultOAuth2User 객체를 생성 후 반환한다.
        즉, OAuth2User = OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
         */

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /*
        userRequest에서 registraionId 추출 후 registraionId로 SocialType 저장
        http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
        userNameAttributeName은 이후 nameAttributeKey로 설정
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);

        // OAuth2 로그인 시 키가 되는 값
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // SocialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);

        Member createdMember = getMember(extractAttributes, socialType);

        // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdMember.getRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdMember.getEmail(),
                createdMember.getRole()
        );
    }

    private SocialType getSocialType(String registrationId) {
        if ("kakao".equals(registrationId)) {
            return SocialType.KAKAO;
        }

        return null;
    }

    private Member getMember(OAuthAttributes attributes, SocialType socialType) {
        Member findMember = memberRepository.findBySocialTypeAndSocialId(socialType,
                attributes.getOAuth2UserInfo().getId()).orElse(null);

        if (findMember == null) {
            return saveMember(attributes, socialType);
        }

        return findMember;
    }

    private Member saveMember(OAuthAttributes attributes, SocialType socialType) {
        Member createdMember = attributes.toEntity(socialType, attributes.getOAuth2UserInfo());
        return memberRepository.save(createdMember);
    }

}
