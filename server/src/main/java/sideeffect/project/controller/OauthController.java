package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;
import sideeffect.project.security.JwtTokenProvider;
import sideeffect.project.service.OauthService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/social")
public class OauthController {

    private final OauthService oauthService;
    private final JwtTokenProvider jwtTokenProvider;
    @PostMapping("/login")
    public ResponseEntity login(@RequestHeader(value = "token") String token,
                                @RequestHeader(value = "providerType") String provider,
                                HttpServletResponse response){
        ProviderType providerType = ProviderType.valueOf(provider.toUpperCase());
        User user = oauthService.login(token, providerType);
        String access_token = jwtTokenProvider.createAccessToken2(user.getEmail(), user.getUserRoleType());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + access_token);
        return new ResponseEntity<>(user.getId(), headers, HttpStatus.OK);
    }
}
