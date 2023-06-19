package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sideeffect.project.common.annotation.ValidImageFile;
import sideeffect.project.common.exception.BaseException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.InvalidValueException;
import sideeffect.project.domain.token.RefreshToken;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.user.RefreshTokenResponse;
import sideeffect.project.dto.user.UserEditResponse;
import sideeffect.project.dto.user.UserRequest;
import sideeffect.project.dto.user.UserResponse;
import sideeffect.project.security.LoginUser;
import sideeffect.project.security.RefreshTokenProvider;
import sideeffect.project.security.UserDetailsImpl;
import sideeffect.project.service.UserService;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RefreshTokenProvider refreshTokenProvider;

    @PostMapping("/join")
    public ResponseEntity<RefreshTokenResponse> join(@RequestBody UserRequest request){

        User user = userService.join(request);
        RefreshToken refreshToken = refreshTokenProvider.createRefreshToken(createToken(user));
        String accessToken = refreshTokenProvider.issueAccessToken(refreshToken.getRefreshToken());

        HttpHeaders headers = createHeaders(refreshToken, accessToken);
        return new ResponseEntity<>(RefreshTokenResponse.of(refreshToken), headers, HttpStatus.OK);
    }

    @GetMapping("/mypage/{id}")
    public UserResponse view(@LoginUser User user, @PathVariable Long id){
        if(id==null) throw new InvalidValueException(ErrorCode.USER_NOT_NULL);
        return userService.findOne(user, id);
    }

    @GetMapping("/editpage")
    public UserEditResponse edit(@LoginUser User user){
        return userService.findEditInfo(user);
    }

    @PatchMapping("/{id}")
    public String update(@LoginUser User user, @PathVariable Long id, @RequestBody UserRequest request){
        userService.update(user, id, request);
        return "update";
    }
    @DeleteMapping("/{id}")
    public String delete(@LoginUser User user, @PathVariable Long id){
        userService.delete(user, id);
        return "delete success";
    }

    @GetMapping("/duple/{nickname}")
    public Boolean duplicate(@PathVariable String nickname){
        return userService.duplicateNickname(nickname);
    }

    @PostMapping("/image")
    public void uploadImage(@LoginUser User user, @ValidImageFile @RequestParam("file") MultipartFile file){
        userService.uploadImage(user, file);
    }

    @GetMapping(value = "/image/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public Resource downloadImage(@PathVariable String filename) {
        try {
            return new UrlResource("file:" + userService.getImageFullPath(filename));
        } catch (IOException e) {
            throw new BaseException(ErrorCode.RECRUIT_BOARD_FILE_DOWNLOAD_FAILED);
        }
    }

    @PostMapping("/image/basic")
    public void toBaseImage(@LoginUser User user){
        userService.toBaseImage(user);
    }
    
    private HttpHeaders createHeaders(RefreshToken refreshToken, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.SET_COOKIE, createCookie(refreshToken.getRefreshToken()).toString());
        return headers;
    }

    private Authentication createToken(User user) {
        UserDetailsImpl userDetails = UserDetailsImpl.of(user);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    private ResponseCookie createCookie(String refreshToken) {
        return ResponseCookie.from("token", refreshToken)
                .sameSite("None")
                .secure(true)
                .path("/api/token/at-issue")
                .httpOnly(true)
                .build();
    }

}
