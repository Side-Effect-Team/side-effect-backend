package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sideeffect.project.dto.recruit.RecruitBoardResponse;
import sideeffect.project.dto.recruit.RecruitBoardScrollRequest;
import sideeffect.project.dto.recruit.RecruitBoardScrollResponse;
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

}
