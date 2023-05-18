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

    @GetMapping(value = "/image/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public Resource downloadImage(@PathVariable String filename) {
        try {
            return new UrlResource("file:" + recruitBoardService.getImageFullPath(filename));
        } catch (IOException e) {
            throw new BaseException(ErrorCode.RECRUIT_BOARD_FILE_DOWNLOAD_FAILED);
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public RecruitBoardResponse registerRecruitBoard(
            @LoginUser User user,
            @Valid @RequestPart RecruitBoardRequest request,
            @ValidImageFile @RequestPart MultipartFile imgFile
            ) {
        return recruitBoardService.register(user, request, imgFile);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public void updateRecruitBoard(
            @LoginUser User user,
            @PathVariable("id") Long boardId,
            @Valid @RequestPart RecruitBoardUpdateRequest request,
            @ValidImageFile @RequestPart MultipartFile imgFile
    ) {
        recruitBoardService.updateRecruitBoard(user.getId(), boardId, request, imgFile);
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
