package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sideeffect.project.dto.user.TokenInfo;
import sideeffect.project.dto.user.UserJoinRequest;
import sideeffect.project.dto.user.UserLoginRequest;
import sideeffect.project.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public String join(@RequestBody UserJoinRequest request){
        userService.join(request);
        return "success";
    }
}
