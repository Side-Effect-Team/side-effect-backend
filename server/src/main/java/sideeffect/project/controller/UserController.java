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
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public String join(@RequestBody UserRequest request){
        System.out.println(request.toString());
        userService.join(request);
        return "join success";
    }

    @GetMapping("/user/{id}")
    public UserResponse view(@LoginUser User user, @PathVariable Long id){
        return userService.findOne(user, id);
    }

    @PatchMapping("/user/{id}")
    public String update(@LoginUser User user, @PathVariable Long id, @RequestBody UserRequest request){
        userService.update(user, id, request);
        return "update success";
    }
    @DeleteMapping("/user/{id}")
    public String delete(@LoginUser User user, @PathVariable Long id){
        userService.delete(user, id);
        return "delete success";
    }


}
