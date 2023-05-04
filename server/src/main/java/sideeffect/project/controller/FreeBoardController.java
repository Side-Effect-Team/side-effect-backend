package sideeffect.project.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sideeffect.project.dto.freeboard.FreeBoardKeyWordRequest;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollRequest;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.service.FreeBoardService;

@RestController
@RequestMapping("/api/free-boards")
@RequiredArgsConstructor
public class FreeBoardController {

    private final FreeBoardService freeBoardService;

    @GetMapping("/{id}")
    public FreeBoardResponse findBoard(@PathVariable Long id) {
        return freeBoardService.findBoard(id);
    }

    @GetMapping("/scroll")
    public FreeBoardScrollResponse scrollBoard(@ModelAttribute FreeBoardScrollRequest request) {
        return freeBoardService.findScroll(request);
    }

    @GetMapping("/search")
    public FreeBoardScrollResponse searchBoard(@ModelAttribute FreeBoardKeyWordRequest request) {
        return freeBoardService.findScrollWithKeyword(request);
    }

    @GetMapping("/rank")
    public List<FreeBoardResponse> getRankBoard() {
        return freeBoardService.findRankFreeBoards();
    }
}
