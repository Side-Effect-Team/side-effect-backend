package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollRequest;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.service.FreeBoardService;

@RestController
@RequestMapping("/api/free-board")
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
}
