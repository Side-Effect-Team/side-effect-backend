package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.InvalidValueException;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.applicant.ApplicantStatus;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.applicant.*;
import sideeffect.project.repository.ApplicantRepository;
import sideeffect.project.repository.BoardPositionRepository;
import sideeffect.project.repository.RecruitBoardRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicantService {

    private final ApplicantRepository applicantRepository;
    private final RecruitBoardRepository recruitBoardRepository;
    private final BoardPositionRepository boardPositionRepository;

    @Transactional
    public ApplicantResponse register(User user, ApplicantRequest request) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(request.getRecruitBoardId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        BoardPosition findBoardPosition = boardPositionRepository.findById(request.getBoardPositionId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOARD_POSITION_NOT_FOUND));

        isOwnedByUser(findRecruitBoard, user.getId());
        isDuplicateApplicant(request.getRecruitBoardId(), user.getId());

        Applicant applicant = request.toApplicant();
        applicant.associate(user, findBoardPosition);

        return ApplicantResponse.of(applicantRepository.save(applicant));
    }

    @Transactional(readOnly = true)
    public Map<String, ApplicantPositionResponse> findApplicants(Long userId, Long boardId, ApplicantStatus status) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));

        validateOwner(findRecruitBoard, userId);

        List<ApplicantListResponse> applicantListResponses = recruitBoardRepository.getApplicantsByPosition(findRecruitBoard.getId(), status);
        return ApplicantPositionResponse.mapOf(applicantListResponses);
    }

    @Transactional
    public void approveApplicant(Long userId, ApplicantUpdateRequest applicantUpdateRequest) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(applicantUpdateRequest.getRecruitBoardId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        Applicant findApplicant = applicantRepository.findById(applicantUpdateRequest.getApplicantId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.APPLICANT_NOT_FOUND));
        BoardPosition findBoardPosition = boardPositionRepository.findBoardPositionIfRecruitable(applicantUpdateRequest.getApplicantId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOARD_POSITION_FULL));

        validateOwner(findRecruitBoard, userId);

        if(checkIfApplicantIdExists(findRecruitBoard.getId(), findApplicant.getId())) {
            throw new InvalidValueException(ErrorCode.APPLICANT_EXISTS);
        }

        findApplicant.updateStatus(applicantUpdateRequest.getStatus());
        findBoardPosition.increaseCurrentNumber();
    }

    @Transactional
    public void rejectApplicant(Long userId, ApplicantUpdateRequest applicantUpdateRequest) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(applicantUpdateRequest.getRecruitBoardId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));

        validateOwner(findRecruitBoard, userId);

        Applicant findApplicant = applicantRepository.findById(applicantUpdateRequest.getApplicantId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.APPLICANT_NOT_FOUND));

        findApplicant.updateStatus(applicantUpdateRequest.getStatus());
    }

    @Transactional
    public void releaseApplicant(Long userId, ApplicantReleaseRequest applicantReleaseRequest) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(applicantReleaseRequest.getRecruitBoardId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));

        validateOwner(findRecruitBoard, userId);

        Applicant findApplicant = applicantRepository.findById(applicantReleaseRequest.getApplicantId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.APPLICANT_NOT_FOUND));

        if(!checkIfApplicantIdExists(findRecruitBoard.getId(), findApplicant.getId())) {
            throw new InvalidValueException(ErrorCode.APPLICANT_NOT_EXISTS);
        }

        BoardPosition findBoardPosition = boardPositionRepository.findByApplicantId(findApplicant.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOARD_POSITION_NOT_FOUND));

        findApplicant.updateStatus(ApplicantStatus.REJECTED);
        findBoardPosition.decreaseCurrentNumber();
    }

    private void isDuplicateApplicant(Long recruitBoardId, Long userId) {
        if(recruitBoardRepository.existsApplicantByRecruitBoard(recruitBoardId, userId)) {
            throw new AuthException(ErrorCode.APPLICANT_DUPLICATED);
        }
    }

    private void isOwnedByUser(RecruitBoard recruitBoard, Long userId) {
        if(userId.equals(recruitBoard.getUser().getId())) {
            throw new AuthException(ErrorCode.APPLICANT_SELF_UNAUTHORIZED);
        }
    }

    private void validateOwner(RecruitBoard recruitBoard, Long userId) {
        if (!userId.equals(recruitBoard.getUser().getId())) {
            throw new AuthException(ErrorCode.APPLICANT_UNAUTHORIZED);
        }
    }

    private boolean checkIfApplicantIdExists(Long RecruitBoardId, Long targetApplicantId) {
        List<ApplicantListResponse> applicantListResponses = recruitBoardRepository.getApplicantsByPosition(RecruitBoardId, ApplicantStatus.APPROVED);
        return applicantListResponses.stream().anyMatch(a -> a.getApplicantId().equals(targetApplicantId));
    }

}
