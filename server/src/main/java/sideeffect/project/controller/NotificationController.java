package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.notification.NotificationResponse;
import sideeffect.project.dto.notification.NotificationScrollResponse;
import sideeffect.project.security.EmptyUser;
import sideeffect.project.security.LoginUser;
import sideeffect.project.service.NotificationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    @GetMapping
    public List<NotificationResponse> view(@LoginUser User user){
        return notificationService.view(user);
    }

    @PostMapping("/{id}")
    public String watch(@LoginUser User user, @PathVariable Long id){
        return notificationService.watch(user, id);
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User user, @PathVariable Long id){
        return notificationService.delete(user, id);
    }

    @GetMapping("/scroll/{lastId}")
    public NotificationScrollResponse scroll(@LoginUser User user, @PathVariable Long lastId){
        return notificationService.scroll(user, lastId);
    }

    @GetMapping("/view-count")
    public int viewCount(@LoginUser User user){
        if(user instanceof EmptyUser) throw new AuthException(ErrorCode.USER_UNAUTHORIZED);
        return notificationService.getViewCount(user);
    }
}
