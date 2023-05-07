package sideeffect.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.applicant.ApplicantStatus;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.ProgressType;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.recruit.RecruitBoardType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.applicant.ApplicantListResponse;
import sideeffect.project.dto.applicant.ApplicantPositionResponse;
import sideeffect.project.dto.applicant.ApplicantRequest;
import sideeffect.project.repository.ApplicantRepository;
import sideeffect.project.repository.BoardPositionRepository;
import sideeffect.project.repository.RecruitBoardRepository;
import sideeffect.project.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicantServiceTest {

    @InjectMocks
    private ApplicantService applicantService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecruitBoardRepository recruitBoardRepository;

    @Mock
    private BoardPositionRepository boardPositionRepository;

    @Mock
    private ApplicantRepository applicantRepository;

    private User user;
    private RecruitBoard recruitBoard;
    private BoardPosition boardPosition;
    private Applicant applicant;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .nickname("tester")
                .password("1234")
                .userRoleType(UserRoleType.ROLE_USER)
                .email("test@naver.com")
                .build();

        recruitBoard = RecruitBoard.builder()
                .id(1L)
                .title("모집 게시판")
                .contents("모집합니다.")
                .recruitBoardType(RecruitBoardType.PROJECT)
                .progressType(ProgressType.ONLINE)
                .deadline(LocalDateTime.now())
                .expectedPeriod("3개월")
                .build();

        recruitBoard.associateUser(user);

        boardPosition = BoardPosition.builder()
                .id(1L)
                .targetNumber(3)
                .build();

        applicant = Applicant.builder()
                .id(1L)
                .build();
    }

    @DisplayName("모집 게시판의 포지션에 지원한다.")
    @Test
    void registerApplicant() {
        ApplicantRequest request = ApplicantRequest.builder().recruitBoardId(recruitBoard.getId()).boardPositionId(boardPosition.getId()).build();
        User otherUser = User.builder().id(2L).nickname("test2").email("test2@naver.com").build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));
        when(boardPositionRepository.findById(any())).thenReturn(Optional.of(boardPosition));
        when(recruitBoardRepository.existsApplicantByRecruitBoard(any(), any())).thenReturn(false);
        when(applicantRepository.save(any())).thenReturn(applicant);

        applicantService.register(otherUser.getId(), request);

        assertAll(
                () -> verify(userRepository).findById(any()),
                () -> verify(boardPositionRepository).findById(any()),
                () -> verify(applicantRepository).save(any())
        );
    }

    @DisplayName("게시판의 주인은 자신의 글에 지원할 수 없다.")
    @Test
    void registerIsOwnedByUser() {
        ApplicantRequest request = ApplicantRequest.builder().recruitBoardId(recruitBoard.getId()).boardPositionId(boardPosition.getId()).build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));
        when(boardPositionRepository.findById(any())).thenReturn(Optional.of(boardPosition));

        assertThatThrownBy(() -> applicantService.register(recruitBoard.getUser().getId(), request))
                .isInstanceOf(AuthException.class);
    }


    @DisplayName("하나의 게시판에 중복 지원은 할 수 없다.")
    @Test
    void registerIsDuplicateApplicant() {
        ApplicantRequest request = ApplicantRequest.builder().recruitBoardId(recruitBoard.getId()).boardPositionId(boardPosition.getId()).build();
        User otherUser = User.builder().id(2L).nickname("test2").email("test2@naver.com").build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));
        when(boardPositionRepository.findById(any())).thenReturn(Optional.of(boardPosition));
        when(recruitBoardRepository.existsApplicantByRecruitBoard(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> applicantService.register(otherUser.getId(), request))
                .isInstanceOf(AuthException.class);
    }

    @DisplayName("모집 게시판의 지원자 리스트를 조회한다.")
    @MethodSource("generateApplicantListTestAugments")
    @ParameterizedTest
    void findApplicants(List<ApplicantListResponse> applicantListResponses, int boardPositionSize) {
        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));
        when(recruitBoardRepository.getApplicantsByPosition(any(), any())).thenReturn(applicantListResponses);

        Map<PositionType, ApplicantPositionResponse> applicants = applicantService.findApplicants(user.getId(), recruitBoard.getId(), ApplicantStatus.PENDING);
        AtomicInteger count = new AtomicInteger();
        applicants.forEach((key, value) -> count.addAndGet(value.getSize()));

        assertAll(
                () -> verify(recruitBoardRepository).findById(any()),
                () -> verify(recruitBoardRepository).getApplicantsByPosition(any(), any()),
                () -> assertThat(applicants.size()).isEqualTo(boardPositionSize),
                () -> assertThat(applicantListResponses.size()).isEqualTo(count.intValue())

        );
    }

    @DisplayName("모집 게시판의 지원자 리스트는 글 작성자가 아니라면 조회가 불가능하다.")
    @Test
    void findApplicantsByNonOwner() {
        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));

        Long nonOwner = 2L;

        assertThatThrownBy(() -> applicantService.findApplicants(nonOwner, recruitBoard.getId(), ApplicantStatus.PENDING))
                .isInstanceOf(AuthException.class);
    }

    private static Stream<Arguments> generateApplicantListTestAugments() {
        return Stream.of(
                Arguments.arguments(generateApplicantListResponse(List.of(PositionType.FRONTEND, PositionType.BACKEND), 3), 2),
                Arguments.arguments(generateApplicantListResponse(List.of(PositionType.FRONTEND, PositionType.BACKEND, PositionType.DESIGNER), 3), 3),
                Arguments.arguments(generateApplicantListResponse(List.of(PositionType.FRONTEND, PositionType.BACKEND, PositionType.DESIGNER, PositionType.PM), 3), 4),
                Arguments.arguments(generateApplicantListResponse(List.of(PositionType.FRONTEND, PositionType.BACKEND, PositionType.BACKEND), 3), 2),
                Arguments.arguments(generateApplicantListResponse(List.of(PositionType.BACKEND, PositionType.BACKEND, PositionType.BACKEND), 3), 1)
        );
    }
    private static List<ApplicantListResponse> generateApplicantListResponse(List<PositionType> positionTypes, int size) {
        List<ApplicantListResponse> applicantListResponses = new ArrayList<>();
        Long id = 1L;
        for (PositionType positionType : positionTypes) {
            for(int i = 0; i < size; i++) {
                ApplicantListResponse response = ApplicantListResponse.builder().userId(id).applicantId(id).nickName("test" + id).positionType(positionType).build();
                applicantListResponses.add(response);
                id++;
            }
        }
        return applicantListResponses;
    }

}