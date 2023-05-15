package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.like.RecruitLikeResponse;
import sideeffect.project.dto.recruit.*;
import sideeffect.project.security.LoginUser;
import sideeffect.project.service.RecruitBoardService;
import sideeffect.project.service.RecruitLikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruit-board")
public class RecruitBoardController {

    private final RecruitBoardService recruitBoardService;
    private final RecruitLikeService recruitLikeService;

    @GetMapping("/{id}")
    public RecruitBoardResponse findRecruitBoard(@PathVariable Long id) {
        return recruitBoardService.findRecruitBoard(id);
    }

    @GetMapping("/all")
    public RecruitBoardAllResponse findAllRecruitBoard() {
        return recruitBoardService.findAllRecruitBoard();
    }

    @GetMapping("/scroll")
    public RecruitBoardScrollResponse findScrollRecruitBoard(@ModelAttribute RecruitBoardScrollRequest request) {
        return recruitBoardService.findRecruitBoards(request);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public RecruitBoardResponse registerRecruitBoard(@LoginUser User user, @RequestBody RecruitBoardRequest request) {
        return recruitBoardService.register(user, request);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public void updateRecruitBoard(
            @LoginUser User user,
            @PathVariable("id") Long boardId,
            @RequestBody RecruitBoardUpdateRequest request
    ) {
        recruitBoardService.updateRecruitBoard(user.getId(), boardId, request);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{id}/add-position")
    public void addRecruitBoardPosition(
            @LoginUser User user,
            @PathVariable("id") Long boardId,
            @RequestBody BoardPositionRequest request
    ) {
        recruitBoardService.addRecruitBoardPosition(user.getId(), boardId, request);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteRecruitBoard(@LoginUser User user, @PathVariable("id") Long boardId) {
        recruitBoardService.deleteRecruitBoard(user.getId(), boardId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/likes/{id}")
    public RecruitLikeResponse recruitBoardLikes(@LoginUser User user, @PathVariable("id") Long boardId) {
        return recruitLikeService.toggleLike(user.getId(), boardId);
    }

}
