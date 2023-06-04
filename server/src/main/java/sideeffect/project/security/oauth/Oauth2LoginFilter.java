package sideeffect.project.security.oauth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sideeffect.project.domain.user.ProviderType;


@RequiredArgsConstructor
public class Oauth2LoginFilter extends UsernamePasswordAuthenticationFilter {


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("인증 메서드가 지원되지 않습니다." + request.getMethod());
        }

        String token = request.getHeader("token");
        ProviderType providerType = ProviderType.valueOf(request.getHeader("providerType").toUpperCase());

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(token, providerType);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
