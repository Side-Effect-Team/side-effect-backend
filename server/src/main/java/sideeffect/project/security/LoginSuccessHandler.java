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
import sideeffect.project.dto.user.RefreshTokenResponse;

@RequiredArgsConstructor
@Transactional
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RefreshTokenProvider refreshTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException{

        RefreshTokenResponse refreshTokenResponse = refreshTokenProvider.createRefreshToken(authentication);
        String accessToken = refreshTokenProvider.issueAccessToken(refreshTokenResponse.getRefreshToken());
        response.addHeader("Authorization", accessToken);
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(refreshTokenResponse));
    }

    private ResponseCookie createCookie(String refreshToken) {
        return ResponseCookie.from("token", refreshToken)
            .sameSite("None")
            .path("/api/token/at-issue")
            .httpOnly(true)
            .build();
    }
}
