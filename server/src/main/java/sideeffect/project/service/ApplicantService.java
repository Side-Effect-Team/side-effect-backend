package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.applicant.ApplicantStatus;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.applicant.ApplicantListResponse;
import sideeffect.project.dto.applicant.ApplicantPositionResponse;
import sideeffect.project.dto.applicant.ApplicantRequest;
import sideeffect.project.dto.applicant.ApplicantResponse;
import sideeffect.project.repository.ApplicantRepository;
import sideeffect.project.repository.BoardPositionRepository;
import sideeffect.project.repository.RecruitBoardRepository;
import sideeffect.project.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicantService {

    private final ApplicantRepository applicantRepository;
    private final RecruitBoardRepository recruitBoardRepository;
    private final BoardPositionRepository boardPositionRepository;
    private final UserRepository userRepository;

    @Transactional
    public ApplicantResponse register(Long userId, ApplicantRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(request.getRecruitBoardId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        BoardPosition findBoardPosition = boardPositionRepository.findById(request.getBoardPositionId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOARD_POSITION_NOT_FOUND));

        isOwnedByUser(findRecruitBoard, userId);
        isDuplicateApplicant(request.getRecruitBoardId(), userId);

        Applicant applicant = request.toApplicant();
        applicant.associate(findUser, findBoardPosition);

        return ApplicantResponse.of(applicantRepository.save(applicant));
    }

    @Transactional(readOnly = true)
    public Map<PositionType, ApplicantPositionResponse> findApplicants(Long userId, Long boardId, ApplicantStatus status) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));

        validateOwner(findRecruitBoard, userId);

        List<ApplicantListResponse> applicantListResponses = recruitBoardRepository.getApplicantsByPosition(boardId, status);
        return ApplicantPositionResponse.mapOf(applicantListResponses);
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

}
