package com.study.springboot.config.auth;

import com.study.springboot.config.auth.dto.OAuthAttributes;
import com.study.springboot.config.auth.dto.SessionUser;
import com.study.springboot.domain.user.User;
import com.study.springboot.domain.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        /*
        registrationId : 현재 로그인 진행 중인 서비스를 구분하는 코드 ex) 구글인지, 네이버인지
        userNameAttributeName(=PK) : OAuth2 로그인 진행 시 키가 되는 필드값.
        즉, 사용자 정보를 요청할 때 어떤 속성을 사용자 이름으로 사용할지를 설정. 구글의 경우 "sub"
         */

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        /*
        OAuth2UserService를 통해 가져온 OAuth2User의 attributes를 담을 클래스
         */

        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("user", new SessionUser(user));
        /*
        SessionUser : 세션에 사용자 정보를 저장하기 위한 Dto 클래스
         */

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }


    private User saveOrUpdate(OAuthAttributes attributes) {
        // 구글 사용자 정보가 업데이트 되었을때를 대비하여 rngus
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}