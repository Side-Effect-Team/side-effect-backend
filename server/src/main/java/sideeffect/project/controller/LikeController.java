package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.like.LikeResponse;
import sideeffect.project.security.LoginUser;
import sideeffect.project.service.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{id}")
    public LikeResponse like(@LoginUser User user, @PathVariable("id") Long boardId) {
        return likeService.toggleLike(user, boardId);
    }
}
