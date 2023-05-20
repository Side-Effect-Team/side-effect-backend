package sideeffect.project.security.oauth;

import sideeffect.project.dto.user.ResponseUserInfo;

public interface Oauth {
    ResponseUserInfo getUserInfo(String token);

}
