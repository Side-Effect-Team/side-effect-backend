package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.comment.CommentUpdateRequest;
import sideeffect.project.dto.comment.RecruitCommentRequest;
import sideeffect.project.dto.comment.RecruitCommentResponse;
import sideeffect.project.security.LoginUser;
import sideeffect.project.service.RecruitCommentService;

import javax.validation.Valid;

@RequestMapping("/api/recruit-comments")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
public class RecruitCommentController {

    private final RecruitCommentService recruitCommentService;

    @PostMapping
    public RecruitCommentResponse registerComment(@Valid @RequestBody RecruitCommentRequest request, @LoginUser User user) {
        return recruitCommentService.registerComment(request, user);
    }

    @PatchMapping("/{id}")
    public void updateComment(
            @PathVariable("id") Long recruitCommentId,
            @Valid @RequestBody CommentUpdateRequest request,
            @LoginUser User user
    ) {
        recruitCommentService.update(user.getId(), recruitCommentId, request.getContent());
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable("id") Long recruitCommentId, @LoginUser User user) {
        recruitCommentService.delete(user.getId(), recruitCommentId);
    }
}
