package sideeffect.project.security.oauth;

import java.util.Map;

public class GoogleOauth2UserInfo extends Oauth2UserInfo{

    public GoogleOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
