package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.InvalidValueException;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.user.UserEditResponse;
import sideeffect.project.dto.user.UserRequest;
import sideeffect.project.dto.user.UserResponse;
import sideeffect.project.security.LoginUser;
import sideeffect.project.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Long join(@RequestBody UserRequest request){
        return userService.join(request);
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
        return "update success";
    }
    @DeleteMapping("/{id}")
    public String delete(@LoginUser User user, @PathVariable Long id){
        userService.delete(user, id);
        return "delete success";
    }



}
