package sideeffect.project.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.recommend.Recommend;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.recommend.RecommendResult;
import sideeffect.project.dto.recommend.RecommendRequest;
import sideeffect.project.dto.recommend.RecommendResponse;
import sideeffect.project.repository.FreeBoardRepository;
import sideeffect.project.repository.RecommendRepository;
import sideeffect.project.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RecommendRepository recommendRepository;
    private final UserRepository userRepository;
    private final FreeBoardRepository freeBoardRepository;

    @Transactional
    public RecommendResponse toggleRecommend(RecommendRequest request) {
        Optional<Recommend> recommend = recommendRepository.findByUserIdAndFreeBoardId(
            request.getUserId(), request.getFreeBoardId());

        if (recommend.isPresent()) {
            Recommend recommendFound = recommend.get();
            cancelRecommend(recommendFound);
            return RecommendResponse.of(recommendFound, RecommendResult.CANCEL_RECOMMEND);
        }

        return RecommendResponse.of(recommendBoard(request), RecommendResult.RECOMMEND);
    }

    private Recommend recommendBoard(RecommendRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        FreeBoard board = freeBoardRepository.findById(request.getFreeBoardId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FREE_BOARD_NOT_FOUND));
        return recommendRepository.save(Recommend.recommend(user, board));
    }

    private void cancelRecommend(Recommend recommend) {
        FreeBoard freeBoard = recommend.getFreeBoard();
        freeBoard.deleteRecommend(recommend);
    }
}
