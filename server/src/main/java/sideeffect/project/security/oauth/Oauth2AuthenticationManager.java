package sideeffect.project.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.security.UserDetailsImpl;
import sideeffect.project.service.OauthService;

@RequiredArgsConstructor
public class Oauth2AuthenticationManager implements AuthenticationManager {

    private final OauthService oauthService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = authentication.getName();
        ProviderType providerType = (ProviderType) authentication.getCredentials();

        UserDetails user = UserDetailsImpl.of(oauthService.login(token, providerType));

        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }
}
