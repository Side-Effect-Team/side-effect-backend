package sideeffect.project.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.comment.CommentRequest;
import sideeffect.project.dto.comment.CommentResponse;
import sideeffect.project.security.LoginUser;
import sideeffect.project.service.CommentService;

@RequestMapping("/api/comments")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponse registerComment(@Valid @RequestBody CommentRequest request, @LoginUser User user) {
        return commentService.registerComment(request, user);
    }

    @PatchMapping("/{id}")
    public void updateComment(@PathVariable("id") Long commentId,
        @NotBlank @RequestBody String content,
        @LoginUser User user) {
        commentService.update(user.getId(), commentId, content);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable("id") Long commentId, @LoginUser User user) {
        commentService.delete(user.getId(), commentId);
    }
}
