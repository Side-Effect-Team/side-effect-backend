package sideeffect.project.service;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.BaseException;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.InvalidValueException;
import sideeffect.project.common.fileupload.service.FreeBoardUploadService;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.freeboard.FreeBoardKeyWordRequest;
import sideeffect.project.dto.freeboard.FreeBoardRequest;
import sideeffect.project.dto.freeboard.DetailedFreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollDto;
import sideeffect.project.dto.freeboard.FreeBoardScrollRequest;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.dto.freeboard.RankResponse;
import sideeffect.project.repository.FreeBoardRepository;
import sideeffect.project.repository.LikeRepository;

@Service
@RequiredArgsConstructor
public class FreeBoardService {

    private static final int RANK_NUMBER = 8;
    private static final int RANK_DAYS = 30;

    private final FreeBoardRepository repository;
    private final FreeBoardUploadService uploadService;
    private final LikeRepository likeRepository;

    @Transactional
    public FreeBoard register(User user, FreeBoardRequest request) {
        FreeBoard freeBoard = request.toFreeBoard();
        freeBoard.associateUser(user);
        validateDuplicateProjectUrl(request);
        return repository.save(freeBoard);
    }

    @Transactional(readOnly = true)
    public FreeBoardScrollResponse findScroll(FreeBoardScrollRequest request, Long userId) {
        if (request.getLastId() == null || request.getLastId() < 0) {
            return searchScroll(request.toScrollDtoWithoutLastId(), userId);
        }
        return searchScroll(request.toScrollDto(), userId);
    }

    @Transactional(readOnly = true)
    public FreeBoardScrollResponse findScrollWithKeyword(FreeBoardKeyWordRequest request, Long userId) {
        if (request.getLastId() == null || request.getLastId() < 0) {
            return searchScrollWithKeyword(request.toScrollDtoWithoutLastId(), userId);
        }
        return searchScrollWithKeyword(request.toScrollDto(), userId);
    }

    @Transactional(readOnly = true)
    public List<RankResponse> findRankFreeBoards(User user) {
        return repository.searchRankBoard(RANK_NUMBER, RANK_DAYS, user.getId(), ChronoUnit.DAYS);
    }

    @Transactional
    public DetailedFreeBoardResponse findBoard(Long boardId, User user) {
        FreeBoard freeBoard = findFreeBoard(boardId);
        freeBoard.increaseViews();

        if (User.isEmpty(user)) {
            return DetailedFreeBoardResponse.of(freeBoard, false);
        }

        return DetailedFreeBoardResponse.of(freeBoard, likeRepository.existsByUserIdAndFreeBoardId(user.getId(), boardId));
    }

    @Transactional
    public void updateBoard(Long userId, Long boardId, FreeBoardRequest request) {
        FreeBoard freeBoard = findBoardById(boardId);
        validateOwner(userId, freeBoard);
        freeBoard.update(request.toFreeBoard());
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        FreeBoard freeBoard = findBoardById(boardId);
        validateOwner(userId, freeBoard);
        repository.delete(freeBoard);
    }

    @Transactional
    public void uploadImage(User user, Long boardId, MultipartFile file) {
        FreeBoard freeBoard = findBoardById(boardId);
        validateOwner(user.getId(), freeBoard);
        saveImageFile(file, freeBoard);
    }

    public String getFreeBoardImageFullPath(String imagePath) {
        return uploadService.getFullPath(imagePath);
    }

    private FreeBoard findBoardById(Long boardId) {
        return repository.findById(boardId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FREE_BOARD_NOT_FOUND));
    }

    private FreeBoard findFreeBoard(Long boardId) {
        return repository.searchBoardFetchJoin(boardId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FREE_BOARD_NOT_FOUND));
    }

    private FreeBoardScrollResponse searchScrollWithKeyword(FreeBoardScrollDto scrollDto, Long userId) {
        List<FreeBoardResponse> responses = repository.searchScrollWithKeyword(scrollDto, userId);
        return FreeBoardScrollResponse.of(responses, hasNextBoards(responses.size(), scrollDto.getSize()));
    }

    private void validateDuplicateProjectUrl(FreeBoardRequest request) {
        if (repository.existsByProjectUrl(request.getProjectUrl())) {
            throw new InvalidValueException(ErrorCode.FREE_BOARD_DUPLICATE);
        }
    }

    private FreeBoardScrollResponse searchScroll(FreeBoardScrollDto scrollDto, Long userId) {
        List<FreeBoardResponse> responses = repository.searchScroll(scrollDto, userId);
        return FreeBoardScrollResponse.of(responses, hasNextBoards(responses.size(), scrollDto.getSize()));
    }

    private void validateOwner(Long userId, FreeBoard freeBoard) {
        if (!userId.equals(freeBoard.getUser().getId())) {
            throw new AuthException(ErrorCode.FREE_BOARD_UNAUTHORIZED);
        }
    }

    private boolean hasNextBoards(Integer boardsSize, Integer requestSize) {
        if (requestSize == null) {
            return false;
        }
        return boardsSize >= requestSize;
    }

    private void saveImageFile(MultipartFile file, FreeBoard freeBoard) {
        try {
            String filePath = uploadService.storeFile(file);
            freeBoard.changeImageUrl(filePath);
        } catch (IOException e) {
            throw new BaseException(ErrorCode.FREE_BOARD_FILE_UPLOAD_FAILED);
        }
    }
}
