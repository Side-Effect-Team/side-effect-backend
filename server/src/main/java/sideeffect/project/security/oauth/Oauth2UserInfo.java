package sideeffect.project.security.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public abstract class Oauth2UserInfo {
    protected Map<String, Object> attributes;

    public abstract String getEmail();
    public abstract String getImageUrl();
}
