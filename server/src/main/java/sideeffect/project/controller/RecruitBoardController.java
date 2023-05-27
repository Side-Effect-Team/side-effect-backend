package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sideeffect.project.common.annotation.ValidImageFile;
import sideeffect.project.common.exception.BaseException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.like.RecruitLikeResponse;
import sideeffect.project.dto.recruit.*;
import sideeffect.project.security.LoginUser;
import sideeffect.project.service.RecruitBoardService;
import sideeffect.project.service.RecruitLikeService;

import javax.validation.Valid;
import java.io.IOException;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruit-board")
public class RecruitBoardController {

    private final RecruitBoardService recruitBoardService;
    private final RecruitLikeService recruitLikeService;

    @GetMapping("/{id}")
    public DetailedRecruitBoardResponse findRecruitBoard(@PathVariable Long id, @LoginUser User user) {
        return recruitBoardService.findRecruitBoard(id, user);
    }

    @GetMapping("/all")
    public RecruitBoardAllResponse findAllRecruitBoard(@LoginUser User user) {
        return recruitBoardService.findAllRecruitBoard(user);
    }

    @GetMapping("/scroll")
    public RecruitBoardScrollResponse findScrollRecruitBoard(@Valid @ModelAttribute RecruitBoardScrollRequest request, @LoginUser User user) {
        return recruitBoardService.findRecruitBoards(request, user);
    }

    @GetMapping(value = "/image/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public Resource downloadImage(@PathVariable String filename) {
        try {
            return new UrlResource("file:" + recruitBoardService.getImageFullPath(filename));
        } catch (IOException e) {
            throw new BaseException(ErrorCode.RECRUIT_BOARD_FILE_DOWNLOAD_FAILED);
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/image/{id}")
    public void uploadImage(@LoginUser User user, @PathVariable("id") Long boardId, @ValidImageFile @RequestParam("file") MultipartFile file) {
        recruitBoardService.uploadImage(user.getId(), boardId, file);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public RecruitBoardResponse registerRecruitBoard(
            @LoginUser User user,
            @Valid @RequestBody RecruitBoardRequest request
            ) {
        return recruitBoardService.register(user, request);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public void updateRecruitBoard(
            @LoginUser User user,
            @PathVariable("id") Long boardId,
            @Valid @RequestBody RecruitBoardUpdateRequest request
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
        return recruitLikeService.toggleLike(user, boardId);
    }

}
