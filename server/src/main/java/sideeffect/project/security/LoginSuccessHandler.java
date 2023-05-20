package sideeffect.project.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Transactional
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    //private final RefreshTokenService refreshTokenService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.addHeader("Authorization", jwtTokenProvider.createAccessToken(authentication));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        response.getWriter().write(String.valueOf(userDetails.getUser().getId()));
        //response.addHeader("Refresh", refreshTokenService.issueRefreshToken(authentication));
    }
}
