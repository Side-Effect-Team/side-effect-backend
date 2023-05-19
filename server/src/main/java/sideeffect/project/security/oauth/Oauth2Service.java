package sideeffect.project.security.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import sideeffect.project.common.exception.JoinException;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;
import sideeffect.project.repository.UserRepository;
import sideeffect.project.security.UserDetailsImpl;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OauthService loadUser 진입");
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //Provider따라 소셜 로그인
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        ProviderType providerType = ProviderType.valueOf(provider);
        Oauth2UserInfo oauth2UserInfo = Oauth2UserInfoFactory.getUserInfo(providerType, oAuth2User.getAttributes());

        //유저 존재 여부 확인
        String email = oauth2UserInfo.getEmail();
        User findUser = userRepository.findByEmailAndProvider(email, providerType).orElseThrow(() -> new JoinException(email, providerType));

        return UserDetailsImpl.of(findUser, oAuth2User.getAttributes());
    }
}
