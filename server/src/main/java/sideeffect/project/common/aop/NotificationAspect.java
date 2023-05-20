package sideeffect.project.common.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.notification.Notification;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.applicant.ApplicantRequest;
import sideeffect.project.dto.applicant.ApplicantUpdateRequest;
import sideeffect.project.dto.comment.CommentRequest;
import sideeffect.project.dto.user.UserRequest;
import sideeffect.project.repository.ApplicantRepository;
import sideeffect.project.repository.BoardPositionRepository;
import sideeffect.project.repository.FreeBoardRepository;
import sideeffect.project.repository.RecruitBoardRepository;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final FreeBoardRepository freeBoardRepository;
    private final RecruitBoardRepository recruitBoardRepository;
    private final BoardPositionRepository boardPositionRepository;
    private final ApplicantRepository applicantRepository;

    @AfterReturning(value = "execution(* sideeffect.project.service.UserService.join(..)) and args(request)",
            returning = "result")
    public void afterJoin(JoinPoint joinPoint, UserRequest request, Long result){
        log.info(joinPoint.getSignature().getName() + "메서드 실행");
        log.info("email: " + request.getEmail());
        log.info("position: " + request.getPosition());
        log.info(result.toString());
    }

    @AfterReturning(value = "execution(* sideeffect.project.service.CommentService.registerComment(..)) and args(request, user)")
    public void afterRegisterComment(JoinPoint joinPoint, CommentRequest request, User user){
        FreeBoard freeBoard = freeBoardRepository.findById(request.getBoardId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.FREE_BOARD_NOT_FOUND));
        String contents = user.getNickname() + "님이 댓글을 달았습니다";
        Notification notification = Notification.builder()
                                        .user(freeBoard.getUser())
                                        .title(freeBoard.getTitle())
                                        .contents(contents)
                                        .link("/projects/" + request.getBoardId())
                                        .watched(false)
                                        .build();
        freeBoard.getUser().addNotification(notification);
    }

    @AfterReturning(value = "execution(* sideeffect.project.service.ApplicantService.approveApplicant(..)) and args(userId, applicantUpdateRequest)")
    public void afterApproveApplicant(JoinPoint joinPoint, Long userId, ApplicantUpdateRequest applicantUpdateRequest){
        Applicant applicant = applicantRepository.findById(applicantUpdateRequest.getApplicantId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.APPLICANT_NOT_FOUND));
        RecruitBoard recruitBoard = recruitBoardRepository.findById(applicantUpdateRequest.getRecruitBoardId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        String contents = applicant.getBoardPosition().getPosition().getPositionType().getKoreanName() + "에 수락되었습니다";
        Notification notification = Notification.builder()
                .user(applicant.getUser())
                .title(recruitBoard.getTitle())
                .contents(contents)
                .link("/recruits/" + applicantUpdateRequest.getRecruitBoardId())
                .watched(false)
                .build();
        applicant.getUser().addNotification(notification);
    }

    @AfterReturning(value = "execution(* sideeffect.project.service.ApplicantService.rejectApplicant(..)) and args(userId, applicantUpdateRequest)")
    public void afterRejectApplicant(JoinPoint joinPoint, Long userId, ApplicantUpdateRequest applicantUpdateRequest){
        Applicant applicant = applicantRepository.findById(applicantUpdateRequest.getApplicantId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.APPLICANT_NOT_FOUND));
        RecruitBoard recruitBoard = recruitBoardRepository.findById(applicantUpdateRequest.getRecruitBoardId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        String contents = applicant.getBoardPosition().getPosition().getPositionType().getKoreanName() + "에 거절되었습니다";
        Notification notification = Notification.builder()
                .user(applicant.getUser())
                .title(recruitBoard.getTitle())
                .contents(contents)
                .link("/recruits/" + applicantUpdateRequest.getRecruitBoardId())
                .watched(false)
                .build();
        applicant.getUser().addNotification(notification);
    }

    @AfterReturning(value = "execution(* sideeffect.project.service.ApplicantService.register(..)) and args(user, request)")
    public void afterRegister(JoinPoint joinPoint, User user, ApplicantRequest request){
        RecruitBoard recruitBoard = recruitBoardRepository.findById(request.getRecruitBoardId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        BoardPosition boardPosition = boardPositionRepository.findById(request.getBoardPositionId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOARD_POSITION_NOT_FOUND));
        String contents = user.getNickname() + "님이 " + boardPosition.getPosition().getPositionType().getKoreanName() +"에 지원했습니다";
        Notification notification = Notification.builder()
                .user(recruitBoard.getUser())
                .title(recruitBoard.getTitle())
                .contents(contents)
                .link("/recruits/" + request.getRecruitBoardId())
                .watched(false)
                .build();
        recruitBoard.getUser().addNotification(notification);
        log.info("지원 알림 완료");
    }
}
