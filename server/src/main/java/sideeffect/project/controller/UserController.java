package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sideeffect.project.domain.user.User;
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
        return userService.findOne(user, id);
    }

   /* @GetMapping("/editpage/{id}")
    public UserResponse edit(@LoginUser User user, @PathVariable Long id){

        return userService.findOne(user, id);
    }*/

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
