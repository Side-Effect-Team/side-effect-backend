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
import sideeffect.project.domain.notification.NotificationType;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.applicant.ApplicantUpdateRequest;
import sideeffect.project.dto.comment.CommentRequest;
import sideeffect.project.dto.comment.RecruitCommentRequest;
import sideeffect.project.repository.*;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final FreeBoardRepository freeBoardRepository;
    private final RecruitBoardRepository recruitBoardRepository;
    private final BoardPositionRepository boardPositionRepository;
    private final ApplicantRepository applicantRepository;
    private final UserRepository userRepository;

    @AfterReturning(value = "execution(* sideeffect.project.service.CommentService.registerComment(..)) and args(request, user)")
    public void afterRegisterFreeComment(JoinPoint joinPoint, CommentRequest request, User user){
        FreeBoard freeBoard = freeBoardRepository.findById(request.getBoardId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.FREE_BOARD_NOT_FOUND));
        String contents = "님이 댓글을 달았습니다";
        Notification notification = Notification.builder()
                                        .user(freeBoard.getUser())
                                        .sendingUser(user)
                                        .title(freeBoard.getTitle())
                                        .contents(contents)
                                        .link("/projects/" + request.getBoardId())
                                        .watched(false)
                                        .notificationType(NotificationType.COMMENT)
                                        .build();
        freeBoard.getUser().addNotification(notification);
    }

    @AfterReturning(value = "execution(* sideeffect.project.service.RecruitCommentService.registerComment(..)) and args(request, user)")
    public void afterRegisterRecruitComment(JoinPoint joinPoint, RecruitCommentRequest request, User user){
        RecruitBoard recruitBoard = recruitBoardRepository.findById(request.getBoardId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        String contents = "님이 댓글을 달았습니다";
        Notification notification = Notification.builder()
                .user(recruitBoard.getUser())
                .sendingUser(user)
                .title(recruitBoard.getTitle())
                .contents(contents)
                .link("/recruits/" + request.getBoardId())
                .watched(false)
                .notificationType(NotificationType.COMMENT)
                .build();
        recruitBoard.getUser().addNotification(notification);
    }

    @AfterReturning(value = "execution(* sideeffect.project.service.ApplicantService.approveApplicant(..)) and args(userId, applicantUpdateRequest)")
    public void afterApproveApplicant(JoinPoint joinPoint, Long userId, ApplicantUpdateRequest applicantUpdateRequest){
        Applicant applicant = applicantRepository.findById(applicantUpdateRequest.getApplicantId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.APPLICANT_NOT_FOUND));
        RecruitBoard recruitBoard = recruitBoardRepository.findById(applicantUpdateRequest.getRecruitBoardId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        String contents = applicant.getBoardPosition().getPosition().getPositionType().getKoreanName() + "에 수락되었습니다";
        Notification notification = Notification.builder()
                .user(applicant.getUser())
                .sendingUser(user)
                .title(recruitBoard.getTitle())
                .contents(contents)
                .link("/recruits/" + applicantUpdateRequest.getRecruitBoardId())
                .watched(false)
                .notificationType(NotificationType.APPROVE)
                .build();
        applicant.getUser().addNotification(notification);
    }

    @AfterReturning(value = "execution(* sideeffect.project.service.ApplicantService.rejectApplicant(..)) and args(userId, applicantUpdateRequest)")
    public void afterRejectApplicant(JoinPoint joinPoint, Long userId, ApplicantUpdateRequest applicantUpdateRequest){
        Applicant applicant = applicantRepository.findById(applicantUpdateRequest.getApplicantId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.APPLICANT_NOT_FOUND));
        RecruitBoard recruitBoard = recruitBoardRepository.findById(applicantUpdateRequest.getRecruitBoardId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        String contents = applicant.getBoardPosition().getPosition().getPositionType().getKoreanName() + "에 거절되었습니다";
        Notification notification = Notification.builder()
                .user(applicant.getUser())
                .sendingUser(user)
                .contents(contents)
                .link("/recruits/" + applicantUpdateRequest.getRecruitBoardId())
                .watched(false)
                .notificationType(NotificationType.REJECT)
                .build();
        applicant.getUser().addNotification(notification);
    }

    @AfterReturning(value = "execution(* sideeffect.project.service.ApplicantService.register(..)) and args(user, boardPositionId)")
    public void afterRegister(JoinPoint joinPoint, User user, Long boardPositionId){
        BoardPosition boardPosition = boardPositionRepository.findById(boardPositionId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOARD_POSITION_NOT_FOUND));
        //RecruitBoard recruitBoard = recruitBoardRepository.findById(boardPosition.ge).orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        String contents = "님이 " + boardPosition.getPosition().getPositionType().getKoreanName() +"에 지원했습니다";
        Notification notification = Notification.builder()
                .user(boardPosition.getRecruitBoard().getUser())
                .sendingUser(user)
                .title(boardPosition.getRecruitBoard().getTitle())
                .contents(contents)
                .link("/recruits/" + boardPosition.getRecruitBoard().getId())
                .watched(false)
                .notificationType(NotificationType.REGISTER)
                .build();
        boardPosition.getRecruitBoard().getUser().addNotification(notification);
    }
}
