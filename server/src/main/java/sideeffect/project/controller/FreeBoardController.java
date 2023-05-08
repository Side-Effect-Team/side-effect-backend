package sideeffect.project.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.freeboard.FreeBoardKeyWordRequest;
import sideeffect.project.dto.freeboard.FreeBoardRequest;
import sideeffect.project.dto.freeboard.DetailedFreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollRequest;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.security.LoginUser;
import sideeffect.project.service.FreeBoardService;

@RestController
@RequestMapping("/api/free-boards")
@RequiredArgsConstructor
public class FreeBoardController {

    private final FreeBoardService freeBoardService;

    @GetMapping("/{id}")
    public DetailedFreeBoardResponse findBoard(@PathVariable Long id) {
        return freeBoardService.findBoard(id);
    }

    @GetMapping("/scroll")
    public FreeBoardScrollResponse scrollBoard(@ModelAttribute FreeBoardScrollRequest request, @LoginUser User user) {
        return freeBoardService.findScroll(request, user.getId());
    }

    @GetMapping("/search")
    public FreeBoardScrollResponse searchBoard(@ModelAttribute FreeBoardKeyWordRequest request, @LoginUser User user) {
        return freeBoardService.findScrollWithKeyword(request, user.getId());
    }

    @GetMapping("/rank")
    public List<FreeBoardResponse> getRankBoard() {
        return freeBoardService.findRankFreeBoards();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public FreeBoardResponse registerBoard(@RequestBody FreeBoardRequest request, @LoginUser User user) {
        return FreeBoardResponse.of(freeBoardService.register(user, request));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public void updateBoard(@PathVariable("id") Long boardId,
        @RequestBody FreeBoardRequest request,
        @LoginUser User user) {
        freeBoardService.updateBoard(user.getId(), boardId, request);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable("id") Long boardId, @LoginUser User user) {
        freeBoardService.deleteBoard(user.getId(), boardId);
    }
}
