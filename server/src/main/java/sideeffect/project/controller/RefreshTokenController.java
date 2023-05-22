package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sideeffect.project.security.RefreshTokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/at-issue")
    public String issue(@RequestHeader(value = "Refresh") String refreshToken){
        return refreshTokenService.issueAccessToken(refreshToken);
    }

}
