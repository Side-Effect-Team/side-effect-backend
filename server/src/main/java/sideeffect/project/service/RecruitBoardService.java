package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sideeffect.project.common.exception.*;
import sideeffect.project.common.fileupload.service.RecruitUploadService;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.BoardStack;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.recruit.*;
import sideeffect.project.repository.RecruitBoardRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitBoardService {

    private final RecruitBoardRepository recruitBoardRepository;
    private final PositionService positionService;
    private final StackService stackService;
    private final RecruitUploadService recruitUploadService;

    @Transactional
    public RecruitBoardResponse register(User user, RecruitBoardRequest request) {
        RecruitBoard recruitBoard = request.toRecruitBoard();
        recruitBoard.associateUser(user);
        recruitBoard.updateBoardPositions(getBoardPositions(recruitBoard, request.getPositions()));
        recruitBoard.updateBoardStacks(getBoardStacks(recruitBoard, request.getTags()));
        saveImageFile(null, recruitBoard); //기본 이미지 사용

        return RecruitBoardResponse.of(recruitBoardRepository.save(recruitBoard));
    }

    @Transactional
    public DetailedRecruitBoardResponse findRecruitBoard(Long boardId, User user) {
        RecruitBoardAndLikeDto findRecruitBoard = recruitBoardRepository.findByBoardIdAndUserId(boardId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        findRecruitBoard.getRecruitBoard().increaseViews();

        DetailedRecruitBoardResponse detailedRecruitBoardResponse = DetailedRecruitBoardResponse.ofLike(findRecruitBoard);

        if(user.getApplicants() != null && !user.getApplicants().isEmpty()) {
            updateSupportedStatus(detailedRecruitBoardResponse, user.getApplicants());
        }

        return detailedRecruitBoardResponse;
    }

    private void updateSupportedStatus(DetailedRecruitBoardResponse detailedRecruitBoardResponse, List<Applicant> applicants) {
        List<Long> applicantBoardPositionIds = applicants.stream()
                .map(applicant -> applicant.getBoardPosition().getId())
                .collect(Collectors.toList());

        detailedRecruitBoardResponse.getPositions().stream()
                .filter(position -> applicantBoardPositionIds.contains(position.getId()))
                .forEach(DetailedBoardPositionResponse::updateSupported);
    }

    @Transactional(readOnly = true)
    public RecruitBoardAllResponse findAllRecruitBoard(User user) {
        List<RecruitBoardAndLikeDto> allWithLike = recruitBoardRepository.findByAllWithLike(user.getId());

        return RecruitBoardAllResponse.of(RecruitBoardListResponse.listOfLike(allWithLike));
    }

    @Transactional(readOnly = true)
    public RecruitBoardScrollResponse findRecruitBoards(RecruitBoardScrollRequest request, User user) {
        List<RecruitBoardAndLikeDto> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(user.getId(), request.getLastId(), request.getKeyword(), request.validateStackTypes(), request.getSize() + 1);
        boolean hasNext = hasNextRecruitBoards(findRecruitBoards, request.getSize());
        List<RecruitBoardResponse> recruitBoardResponses = RecruitBoardResponse.listOfLike(findRecruitBoards);
        updateClosedStatus(recruitBoardResponses);

        return RecruitBoardScrollResponse.of(recruitBoardResponses, hasNext);
    }

    private void updateClosedStatus(List<RecruitBoardResponse> recruitBoardResponses) {
        recruitBoardResponses.stream()
                .filter(response -> response.getPositionsList() != null && !response.getPositionsList().isEmpty())
                .filter(response -> response.getPositionsList().stream()
                        .allMatch(position -> position.getCurrentNumber() == position.getTargetNumber()))
                .forEach(RecruitBoardResponse::updateClosed);
    }

    @Transactional
    public void updateRecruitBoard(Long userId, Long boardId, RecruitBoardUpdateRequest request) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        validateOwner(userId, findRecruitBoard);
        findRecruitBoard.updateBoardStacks(getBoardStacks(findRecruitBoard, request.getTags()));

        findRecruitBoard.update(request.toRecruitBoard());
    }

    @Transactional
    public void uploadImage(Long userId, Long boardId, MultipartFile file) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        validateOwner(userId, findRecruitBoard);
        recruitUploadService.deleteFile(findRecruitBoard.getImgSrc());
        saveImageFile(file, findRecruitBoard);
    }

    @Transactional
    public void addRecruitBoardPosition(Long userId, Long boardId, BoardPositionRequest request) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        validateOwner(userId, findRecruitBoard);

        BoardPosition targetBoardPosition = toBoardPosition(findRecruitBoard, request);
        isValidPosition(findRecruitBoard, targetBoardPosition);

        findRecruitBoard.addBoardPosition(targetBoardPosition);
    }

    @Transactional
    public void deleteRecruitBoard(Long userId, Long boardId) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        validateOwner(userId, findRecruitBoard);
        recruitBoardRepository.delete(findRecruitBoard);
    }

    public String getImageFullPath(String imagePath) {
        return recruitUploadService.getFullPath(imagePath);
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

    private boolean hasNextRecruitBoards(List<RecruitBoardAndLikeDto> recruitBoards, int requestSize) {
        boolean hasNext = false;

        if(recruitBoards.size() > requestSize) {
            hasNext = true;
            recruitBoards.remove(requestSize);
        }

        return hasNext;
    }

    private void isValidPosition(RecruitBoard recruitBoard, BoardPosition targetBoardPosition) {
        if(recruitBoard.getBoardPositions().stream()
                .anyMatch(boardPosition -> boardPosition.getPosition().getId().equals(targetBoardPosition.getPosition().getId()))) {
            throw new InvalidValueException(ErrorCode.BOARD_POSITION_ALREADY_EXISTS);
        }
    }

    private void saveImageFile(MultipartFile file, RecruitBoard recruitBoard) {
        try {
            String filePath = recruitUploadService.storeFile(file);
            recruitBoard.updateImgSrc(filePath);
        } catch (IOException e) {
            throw new BaseException(ErrorCode.RECRUIT_BOARD_FILE_UPLOAD_FAILED);
        }
    }

}
