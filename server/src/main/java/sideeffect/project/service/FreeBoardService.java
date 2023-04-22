package sideeffect.project.service;

import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.dto.freeboard.FreeBoardRequest;
import sideeffect.project.repository.FreeBoardRepository;

@Service
@RequiredArgsConstructor
public class FreeBoardService {

    private final FreeBoardRepository repository;

    public FreeBoard register(Long userId, FreeBoardRequest request) {
        FreeBoard freeBoard = request.toFreeBoard();
        freeBoard.setUser(userId);
        return repository.save(freeBoard);
    }

    public void updateBoard(Long userId, Long boardId, FreeBoardRequest request) {
        FreeBoard freeBoard = repository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        validateOwner(userId, freeBoard);
        freeBoard.update(request.toFreeBoard());
    }

    public FreeBoard findBoard(Long boardId) {
        return repository.findById(boardId).orElseThrow(EntityNotFoundException::new);
    }

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
}
