package sideeffect.project.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.like.Like;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.like.LikeResult;
import sideeffect.project.dto.like.LikeResponse;
import sideeffect.project.repository.FreeBoardRepository;
import sideeffect.project.repository.LikeRepository;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final FreeBoardRepository freeBoardRepository;

    @Transactional
    public LikeResponse toggleLike(User user, Long boardId) {
        Optional<Like> recommend = likeRepository.searchLike(user.getId(), boardId);

        if (recommend.isPresent()) {
            Like likeFound = recommend.get();
            cancelLike(likeFound);
            return LikeResponse.of(likeFound, LikeResult.CANCEL_LIKE);
        }

        return LikeResponse.of(likeBoard(user, boardId), LikeResult.LIKE);
    }

    private Like likeBoard(User user, Long boardId) {
        FreeBoard board = freeBoardRepository.findById(boardId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FREE_BOARD_NOT_FOUND));
        return likeRepository.save(Like.like(user, board));
    }

    private void cancelLike(Like like) {
        likeRepository.delete(like);
    }
}
