package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.BoardStack;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.recruit.*;
import sideeffect.project.repository.RecruitBoardRepository;
import sideeffect.project.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitBoardService {

    private final RecruitBoardRepository recruitBoardRepository;
    private final UserRepository userRepository;
    private final PositionService positionService;
    private final StackService stackService;

    @Transactional
    public RecruitBoardResponse register(Long userId, RecruitBoardRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        RecruitBoard recruitBoard = request.toRecruitBoard();
        recruitBoard.associateUser(findUser);
        recruitBoard.updateBoardPositions(getBoardPositions(recruitBoard, request.getPositions()));
        recruitBoard.updateBoardStacks(getBoardStacks(recruitBoard, request.getTags()));

        return RecruitBoardResponse.of(recruitBoardRepository.save(recruitBoard));
    }

    @Transactional
    public RecruitBoardResponse findRecruitBoard(Long boardId) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        findRecruitBoard.increaseViews();

        return RecruitBoardResponse.of(findRecruitBoard);
    }

    @Transactional(readOnly = true)
    public RecruitBoardScrollResponse findRecruitBoards(RecruitBoardScrollRequest request) {
        List<RecruitBoard> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(request.getLastId(), request.getKeyword(), request.validateStackTypes(), Pageable.ofSize(request.getSize() + 1));
        boolean hasNext = hasNextRecruitBoards(findRecruitBoards, request.getSize());
        return RecruitBoardScrollResponse.of(RecruitBoardResponse.listOf(findRecruitBoards), hasNext);
    }

    @Transactional
    public void updateRecruitBoard(Long userId, Long boardId, RecruitBoardRequest request) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        validateOwner(userId, findRecruitBoard);
        findRecruitBoard.updateBoardPositions(getBoardPositions(findRecruitBoard, request.getPositions()));
        findRecruitBoard.updateBoardStacks(getBoardStacks(findRecruitBoard, request.getTags()));

        findRecruitBoard.update(request.toRecruitBoard());
    }

    @Transactional
    public void deleteRecruitBoard(Long userId, Long boardId) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        validateOwner(userId, findRecruitBoard);
        recruitBoardRepository.delete(findRecruitBoard);
    }

    private List<BoardPosition> getBoardPositions(RecruitBoard recruitBoard, List<BoardPositionRequest> positionRequests) {
        List<BoardPosition> boardPositions = Collections.emptyList();

        if(positionRequests != null && !positionRequests.isEmpty()) {
            boardPositions = positionRequests.stream()
                    .map(boardPositionRequest -> this.toBoardPosition(recruitBoard, boardPositionRequest))
                    .collect(Collectors.toList());
        }

        return boardPositions;
    }

    private List<BoardStack> getBoardStacks(RecruitBoard recruitBoard, List<StackType> stackRequests) {
        List<BoardStack> boardStacks = Collections.emptyList();

        if(stackRequests != null && !stackRequests.isEmpty()) {
            boardStacks = stackRequests.stream()
                    .map(stackType -> this.toBoardStack(recruitBoard, stackType))
                    .collect(Collectors.toList());
        }

        return boardStacks;
    }

    private BoardPosition toBoardPosition(RecruitBoard recruitBoard, BoardPositionRequest request) {
        Position findPosition = positionService.findByPositionType(request.getPositionType());
        return request.toBoardPosition(recruitBoard, findPosition);
    }

    private BoardStack toBoardStack(RecruitBoard recruitBoard, StackType stackType) {
        Stack findStack = stackService.findByStackType(stackType);
        return BoardStack.builder()
                .recruitBoard(recruitBoard)
                .stack(findStack)
                .build();
    }

    private void validateOwner(Long userId, RecruitBoard recruitBoard) {
        if (!userId.equals(recruitBoard.getUser().getId())) {
            throw new AuthException(ErrorCode.RECRUIT_BOARD_UNAUTHORIZED);
        }
    }

    private boolean hasNextRecruitBoards(List<RecruitBoard> recruitBoards, int requestSize) {
        boolean hasNext = false;

        if(recruitBoards.size() > requestSize) {
            hasNext = true;
            recruitBoards.remove(requestSize);
        }

        return hasNext;
    }

}
