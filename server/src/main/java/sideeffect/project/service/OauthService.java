package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.JoinException;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.user.ResponseUserInfo;
import sideeffect.project.repository.UserRepository;
import sideeffect.project.security.oauth.Oauth;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OauthService {
    private final Map<String, Oauth> oAuthMap;
    private final UserRepository userRepository;

    public User login(String token, ProviderType providerType){
        ResponseUserInfo responseUserInfo = sendToAuthorizationServer(token, providerType);
        return userRepository.findByEmailAndProvider(responseUserInfo.getEmail(), providerType).orElseThrow(() -> new JoinException(responseUserInfo.getEmail(), providerType));
    }

    public ResponseUserInfo sendToAuthorizationServer(String token, ProviderType providerType) {
        switch (providerType) {
            case GOOGLE: return oAuthMap.get("googleOAuth").getUserInfo(token);
            case KAKAO: return oAuthMap.get("kakaoOAuth").getUserInfo(token);
            default: throw new IllegalArgumentException();
        }

    }

}
