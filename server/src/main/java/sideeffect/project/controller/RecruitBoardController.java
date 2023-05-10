package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.recruit.RecruitBoardRequest;
import sideeffect.project.dto.recruit.RecruitBoardResponse;
import sideeffect.project.dto.recruit.RecruitBoardScrollRequest;
import sideeffect.project.dto.recruit.RecruitBoardScrollResponse;
import sideeffect.project.security.LoginUser;
import sideeffect.project.service.RecruitBoardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruit-board")
public class RecruitBoardController {

    private final RecruitBoardService recruitBoardService;

    @GetMapping("/{id}")
    public RecruitBoardResponse findRecruitBoard(@PathVariable Long id) {
        return recruitBoardService.findRecruitBoard(id);
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
            @RequestBody RecruitBoardRequest request
    ) {
        recruitBoardService.updateRecruitBoard(user.getId(), boardId, request);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteRecruitBoard(@LoginUser User user, @PathVariable("id") Long boardId) {
        recruitBoardService.deleteRecruitBoard(user.getId(), boardId);
    }

}
