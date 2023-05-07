package sideeffect.project.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.freeboard.FreeBoardKeyWordRequest;
import sideeffect.project.dto.freeboard.FreeBoardRequest;
import sideeffect.project.dto.freeboard.DetailedFreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollRequest;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.repository.FreeBoardRepository;

@Service
@RequiredArgsConstructor
public class FreeBoardService {

    private static final int RANK_NUMBER = 6;

    private final FreeBoardRepository repository;

    @Transactional
    public FreeBoard register(User user, FreeBoardRequest request) {
        FreeBoard freeBoard = request.toFreeBoard();
        freeBoard.associateUser(user);
        return repository.save(freeBoard);
    }

    @Transactional(readOnly = true)
    public FreeBoardScrollResponse findScroll(FreeBoardScrollRequest request) {
        if (request.getLastId() == null) {
            return findStartScrollOfBoards(request);
        }
        return findBoards(request);
    }

    @Transactional(readOnly = true)
    public FreeBoardScrollResponse findScrollWithKeyword(FreeBoardKeyWordRequest request) {
        if (request.getLastId() == null) {
            return findStartScrollBoardWithKeyword(request);
        }
        return findScrollBoardWithKeyword(request);
    }

    @Transactional(readOnly = true)
    public List<FreeBoardResponse> findRankFreeBoards() {
        return FreeBoardResponse.listOf(repository.findRankFreeBoard(Pageable.ofSize(RANK_NUMBER)));
    }

    @Transactional(readOnly = true)
    public DetailedFreeBoardResponse findBoard(Long boardId) {
        FreeBoard freeBoard = findFreeBoard(boardId);
        freeBoard.increaseViews();
        return DetailedFreeBoardResponse.of(freeBoard);
    }

    @Transactional
    public void updateBoard(Long userId, Long boardId, FreeBoardRequest request) {
        FreeBoard freeBoard = findFreeBoard(boardId);
        validateOwner(userId, freeBoard);
        freeBoard.update(request.toFreeBoard());
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        FreeBoard freeBoard = findFreeBoard(boardId);
        validateOwner(userId, freeBoard);
        repository.delete(freeBoard);
    }

    private FreeBoard findFreeBoard(Long boardId) {
        return repository.findById(boardId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FREE_BOARD_NOT_FOUND));
    }

    private FreeBoardScrollResponse findBoards(FreeBoardScrollRequest request) {
        List<FreeBoard> freeBoards = repository
            .findByIdLessThanOrderByIdDesc(request.getLastId(), Pageable.ofSize(request.getSize()));
        return FreeBoardScrollResponse
            .of(FreeBoardResponse.listOf(freeBoards), hasNextBoards(freeBoards, request.getSize()));
    }

    private FreeBoardScrollResponse findStartScrollOfBoards(FreeBoardScrollRequest request) {
        List<FreeBoard> freeBoards = repository.findStartScrollOfBoard(Pageable.ofSize(request.getSize()));
        return FreeBoardScrollResponse
            .of(FreeBoardResponse.listOf(freeBoards), hasNextBoards(freeBoards, request.getSize()));
    }

    private FreeBoardScrollResponse findScrollBoardWithKeyword(FreeBoardKeyWordRequest request) {
        List<FreeBoard> freeBoards = repository
            .findScrollOfBoardsWithKeyWord(request.getKeyWord(), request.getLastId(),
                Pageable.ofSize(request.getSize()));
        return FreeBoardScrollResponse
            .of(FreeBoardResponse.listOf(freeBoards), hasNextBoards(freeBoards, request.getSize()));
    }

    private FreeBoardScrollResponse findStartScrollBoardWithKeyword(FreeBoardKeyWordRequest request) {
        List<FreeBoard> freeBoards = repository
            .findStartScrollOfBoardsWithKeyWord(request.getKeyWord(), Pageable.ofSize(request.getSize()));
        return FreeBoardScrollResponse
            .of(FreeBoardResponse.listOf(freeBoards), hasNextBoards(freeBoards, request.getSize()));
    }

    private void validateOwner(Long userId, FreeBoard freeBoard) {
        if (!userId.equals(freeBoard.getUser().getId())) {
            throw new AuthException(ErrorCode.FREE_BOARD_UNAUTHORIZED);
        }
    }

    private boolean hasNextBoards(List<FreeBoard> boards, int requestSize) {
        return boards.size() >= requestSize;
    }
}
