package sideeffect.project.service;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.dto.freeboard.FreeBoardKeyWordRequest;
import sideeffect.project.dto.freeboard.FreeBoardRequest;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollRequest;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.repository.FreeBoardRepository;

@Service
@RequiredArgsConstructor
public class FreeBoardService {

    private final FreeBoardRepository repository;

    @Transactional
    public FreeBoard register(Long userId, FreeBoardRequest request) {
        FreeBoard freeBoard = request.toFreeBoard();
        freeBoard.setUser(userId);
        return repository.save(freeBoard);
    }

    @Transactional(readOnly = true)
    public FreeBoardScrollResponse findBoardScroll(FreeBoardScrollRequest request) {
        if (request.getLastId() == null) {
            List<FreeBoard> freeBoards = repository.findLastPagingBoards(Pageable.ofSize(request.getSize()));
            return FreeBoardScrollResponse
                .of(FreeBoardResponse.listOf(freeBoards), hasNextBoards(freeBoards, request.getSize()));
        }
        List<FreeBoard> freeBoards = repository
            .findByIdLessThanOrderByIdDesc(request.getLastId(), Pageable.ofSize(request.getSize()));
        return FreeBoardScrollResponse
            .of(FreeBoardResponse.listOf(freeBoards), hasNextBoards(freeBoards, request.getSize()));
    }

    @Transactional(readOnly = true)
    public FreeBoardScrollResponse findBoardWithKeywordScroll(FreeBoardKeyWordRequest request) {
        if (request.getLastId() == null) {
            List<FreeBoard> freeBoards = repository.findFreeBoardWithKeyWord(request.getKeyWord(),
                Pageable.ofSize(request.getSize()));
            return FreeBoardScrollResponse
                .of(FreeBoardResponse.listOf(freeBoards), hasNextBoards(freeBoards, request.getSize()));
        }
        List<FreeBoard> freeBoards = repository
            .findFreeBoardScrollWithKeyWord(request.getKeyWord(), request.getLastId(),
                Pageable.ofSize(request.getSize()));
        return FreeBoardScrollResponse
            .of(FreeBoardResponse.listOf(freeBoards), hasNextBoards(freeBoards, request.getSize()));
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

    private void validateOwner(Long userId, FreeBoard freeBoard) {
        if (!userId.equals(freeBoard.getUserId())) {
            throw new IllegalArgumentException("게시글의 주인이 아닙니다.");
        }
    }

    private boolean hasNextBoards(List<FreeBoard> boards, int requestSize) {
        return boards.size() >= requestSize;
    }
}
