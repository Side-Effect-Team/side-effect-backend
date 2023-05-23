package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sideeffect.project.security.RefreshTokenProvider;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class RefreshTokenController {

    private final RefreshTokenProvider refreshTokenProvider;

    @PostMapping("/at-issue")
    public String issue(@RequestHeader(value = "Refresh") String refreshToken){
        return refreshTokenProvider.issueAccessToken(refreshToken);
    }

}
