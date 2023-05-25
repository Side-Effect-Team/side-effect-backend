package sideeffect.project.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import sideeffect.project.domain.token.RefreshToken;
import sideeffect.project.dto.user.RefreshTokenResponse;

@RequiredArgsConstructor
@Transactional
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RefreshTokenProvider refreshTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException{

        RefreshToken refreshToken = refreshTokenProvider.createRefreshToken(authentication);
        String accessToken = refreshTokenProvider.issueAccessToken(refreshToken.getRefreshToken());
        response.addHeader("Authorization", accessToken);
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(RefreshTokenResponse.of(refreshToken)));
    }

    private ResponseCookie createCookie(String refreshToken) {
        return ResponseCookie.from("token", refreshToken)
            .sameSite("None")
            .path("/api/token/at-issue")
            .httpOnly(true)
            .build();
    }
}
