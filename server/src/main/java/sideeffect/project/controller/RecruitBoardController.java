package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sideeffect.project.dto.recruit.RecruitBoardResponse;
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

}
