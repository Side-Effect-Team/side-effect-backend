package sideeffect.project.security;

import javax.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Transactional
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RefreshTokenProvider refreshTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException{

        String refreshToken = refreshTokenProvider.createRefreshToken(authentication);
        String accessToken = refreshTokenProvider.issueAccessToken(refreshToken);
        response.addHeader("Authorization", accessToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        response.getWriter().write(String.valueOf(userDetails.getUser().getId()));
        response.addCookie(createCookie(refreshToken));
    }

    private Cookie createCookie(String refreshToken) {
        Cookie cookie = new Cookie("token", refreshToken);
        cookie.setPath("/api/token/at-issue");
        cookie.setHttpOnly(true);
        return cookie;
    }

}
