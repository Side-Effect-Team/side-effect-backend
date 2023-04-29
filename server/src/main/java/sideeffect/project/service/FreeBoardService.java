package sideeffect.project.service;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.freeboard.FreeBoardKeyWordRequest;
import sideeffect.project.dto.freeboard.FreeBoardRequest;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollRequest;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.repository.FreeBoardRepository;
import sideeffect.project.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FreeBoardService {

    private final FreeBoardRepository repository;
    private final UserRepository userRepository;

    @Transactional
    public FreeBoard register(Long userId, FreeBoardRequest request) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
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

    @Transactional
    public void updateBoard(Long userId, Long boardId, FreeBoardRequest request) {
        FreeBoard freeBoard = repository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        validateOwner(userId, freeBoard);
        freeBoard.update(request.toFreeBoard());
    }

    @Transactional
    public FreeBoardResponse findBoard(Long boardId) {
        FreeBoard freeBoard = repository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        freeBoard.increaseViews();
        return FreeBoardResponse.of(freeBoard);
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        FreeBoard freeBoard = repository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        validateOwner(userId, freeBoard);
        repository.delete(freeBoard);
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
            throw new IllegalArgumentException("게시글의 주인이 아닙니다.");
        }
    }

    private boolean hasNextBoards(List<FreeBoard> boards, int requestSize) {
        return boards.size() >= requestSize;
    }
}
