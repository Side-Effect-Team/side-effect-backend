package sideeffect.project.controller;

import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sideeffect.project.security.RefreshTokenProvider;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class RefreshTokenController {

    private final RefreshTokenProvider refreshTokenProvider;

    @PostMapping("/at-issue")
    public void issue(@RequestHeader(name = "cookie") String refreshToken,
        HttpServletResponse response){
        String accessToken = refreshTokenProvider.issueAccessToken(getToken(refreshToken));
        response.addHeader("Authorization", accessToken);
    }

    private String getToken(String refreshToken) {
        return refreshToken.substring(6);
    }
}
