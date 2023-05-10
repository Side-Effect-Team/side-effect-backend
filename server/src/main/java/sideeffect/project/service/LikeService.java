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
import sideeffect.project.dto.like.LikeRequest;
import sideeffect.project.dto.like.LikeResponse;
import sideeffect.project.repository.FreeBoardRepository;
import sideeffect.project.repository.LikeRepository;
import sideeffect.project.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final FreeBoardRepository freeBoardRepository;

    @Transactional
    public LikeResponse toggleLike(LikeRequest request) {
        Optional<Like> recommend = likeRepository.findByUserIdAndFreeBoardId(
            request.getUserId(), request.getFreeBoardId());

        if (recommend.isPresent()) {
            Like likeFound = recommend.get();
            cancelLike(likeFound);
            return LikeResponse.of(likeFound, LikeResult.CANCEL_LIKE);
        }

        return LikeResponse.of(likeBoard(request), LikeResult.LIKE);
    }

    private Like likeBoard(LikeRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        FreeBoard board = freeBoardRepository.findById(request.getFreeBoardId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FREE_BOARD_NOT_FOUND));
        return likeRepository.save(Like.like(user, board));
    }

    private void cancelLike(Like like) {
        FreeBoard freeBoard = like.getFreeBoard();
        freeBoard.deleteLike(like);
    }
}
