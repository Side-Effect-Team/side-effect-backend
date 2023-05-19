package sideeffect.project.security.oauth;

import java.util.Map;

public class NaverOauth2UserInfo extends Oauth2UserInfo{
    public NaverOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if(response==null)
            return null;

        return (String) response.get("email");
    }

    @Override
    public String getImageUrl() {
        return null;
    }
}
