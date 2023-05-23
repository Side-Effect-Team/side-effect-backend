package sideeffect.project.controller;

import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sideeffect.project.common.annotation.ValidImageFile;
import sideeffect.project.common.exception.BaseException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.freeboard.FreeBoardKeyWordRequest;
import sideeffect.project.dto.freeboard.FreeBoardRequest;
import sideeffect.project.dto.freeboard.DetailedFreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollRequest;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.dto.freeboard.OrderType;
import sideeffect.project.security.LoginUser;
import sideeffect.project.service.FreeBoardService;

@Validated
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
    public FreeBoardScrollResponse scrollBoard(@RequestParam(defaultValue = "-1") Long lastId,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, name = "filter", defaultValue = "LATEST") OrderType orderType,
        @LoginUser User user) {
        if (keyword == null) {
            FreeBoardScrollRequest scrollRequest = FreeBoardScrollRequest.builder()
                .orderType(orderType).size(size).lastId(lastId).build();
            return searchScroll(scrollRequest, user);
        }
        FreeBoardKeyWordRequest request = FreeBoardKeyWordRequest.builder()
            .lastId(lastId).size(size).keyword(keyword).orderType(orderType).build();
        return searchScrollWithKeyword(request, user);
    }

    @GetMapping("/rank")
    public List<FreeBoardResponse> getRankBoard(@LoginUser User user) {
        return freeBoardService.findRankFreeBoards(user);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public FreeBoardResponse registerBoard(@Valid @RequestBody FreeBoardRequest request, @LoginUser User user) {
        return FreeBoardResponse.of(freeBoardService.register(user, request));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/image/{id}")
    public void uploadImage(@LoginUser User user, @PathVariable("id") Long boardId,
        @ValidImageFile @RequestParam(value = "file", required = false) MultipartFile file) {
        freeBoardService.uploadImage(user, boardId, file);
    }

    @GetMapping(value = "/image/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public Resource downloadImage(@PathVariable String filename) {
        try {
            return new UrlResource("file:" + freeBoardService.getFreeBoardImageFullPath(filename));
        } catch (IOException e) {
            throw new BaseException(ErrorCode.RECRUIT_BOARD_FILE_DOWNLOAD_FAILED);
        }
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

    private FreeBoardScrollResponse searchScrollWithKeyword(FreeBoardKeyWordRequest request, User user) {
        return freeBoardService.findScrollWithKeyword(request, user.getId());
    }

    private FreeBoardScrollResponse searchScroll(FreeBoardScrollRequest request, User user) {
        return freeBoardService.findScroll(request, user.getId());
    }
}
