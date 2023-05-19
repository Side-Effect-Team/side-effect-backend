package sideeffect.project.security.oauth;

import sideeffect.project.common.exception.InvalidValueException;
import sideeffect.project.domain.user.ProviderType;

import java.util.Map;

public class Oauth2UserInfoFactory {
    public static Oauth2UserInfo getUserInfo(ProviderType providerType, Map<String, Object> attributes){
        switch (providerType){
            case GOOGLE: return new GoogleOauth2UserInfo(attributes);
            case KAKAO: return new KakaoOauth2UserInfo(attributes);
            case NAVER: return new NaverOauth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type");
        }
    }
}
