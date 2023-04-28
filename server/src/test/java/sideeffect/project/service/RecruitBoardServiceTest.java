package sideeffect.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.ProgressType;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.recruit.RecruitBoardType;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackLevelType;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.recruit.BoardPositionRequest;
import sideeffect.project.dto.recruit.BoardStackRequest;
import sideeffect.project.dto.recruit.RecruitBoardRequest;
import sideeffect.project.repository.RecruitBoardRepository;
import sideeffect.project.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruitBoardServiceTest {

    @InjectMocks
    private RecruitBoardService recruitBoardService;

    @Mock
    private RecruitBoardRepository recruitBoardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PositionService positionService;

    @Mock
    private StackService stackService;

    private User user;
    private RecruitBoard recruitBoard;
    private Position position;
    private Stack stack;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("testName")
                .nickname("test")
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

        position = Position.builder()
                .id(1L)
                .positionType(PositionType.BACKEND)
                .build();

        stack = Stack.builder()
                .id(1L)
                .stackType(StackType.SPRING)
                .build();
    }

    @DisplayName("모집 게시판을 저장한다.")
    @Test
    void register() {
        RecruitBoardRequest request = RecruitBoardRequest.builder()
                .title("모집 게시판 제목")
                .contents("모집합니다.")
                .recruitBoardType(RecruitBoardType.PROJECT)
                .progressType(ProgressType.ONLINE)
                .deadline(LocalDateTime.now())
                .expectedPeriod("3개월")
                .positions(List.of(new BoardPositionRequest(PositionType.BACKEND, 3)))
                .stacks(List.of(new BoardStackRequest(StackType.SPRING, StackLevelType.LOW)))
                .build();

        Long userId = 1L;

        when(recruitBoardRepository.save(any())).thenReturn(recruitBoard);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(positionService.findByPositionType(any())).thenReturn(position);
        when(stackService.findByStackType(any())).thenReturn(stack);

        recruitBoardService.register(userId, request);

        verify(recruitBoardRepository).save(any());
    }

    @DisplayName("모집 게시판을 조회한다.")
    @Test
    void getRecruitBoard() {
        int beforeViews = recruitBoard.getViews();

        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));

        recruitBoardService.findRecruitBoard(1L);

        assertAll(
                () -> verify(recruitBoardRepository).findById(any()),
                () -> assertThat(recruitBoard.getViews()).isEqualTo(beforeViews + 1)
        );
    }

    @DisplayName("모집 게시판을 업데이트한다.")
    @Test
    public void updateRecruitBoard() {
        RecruitBoardRequest request = RecruitBoardRequest.builder()
                .title("수정된 제목")
                .contents("수정된 내용")
                .recruitBoardType(RecruitBoardType.STUDY)
                .progressType(ProgressType.OFFLINE)
                .deadline(LocalDateTime.now())
                .expectedPeriod("5개월")
                .positions(List.of(
                        new BoardPositionRequest(PositionType.FRONTEND, 3),
                        new BoardPositionRequest(PositionType.BACKEND, 2)
                ))
                .stacks(List.of(
                        new BoardStackRequest(StackType.SPRING, StackLevelType.LOW),
                        new BoardStackRequest(StackType.JAVA, StackLevelType.LOW)
                ))
                .build();

        Long boardId = 1L;
        Long userId = 1L;

        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));

        recruitBoardService.updateRecruitBoard(userId, boardId, request);

        assertAll(
                () -> verify(recruitBoardRepository).findById(any()),
                () -> assertThat(recruitBoard.getTitle()).isEqualTo(request.getTitle()),
                () -> assertThat(recruitBoard.getContents()).isEqualTo(request.getContents()),
                () -> assertThat(recruitBoard.getRecruitBoardType()).isEqualTo(request.getRecruitBoardType()),
                () -> assertThat(recruitBoard.getProgressType()).isEqualTo(request.getProgressType()),
                () -> assertThat(recruitBoard.getExpectedPeriod()).isEqualTo(request.getExpectedPeriod()),
                () -> assertThat(recruitBoard.getDeadline()).isEqualTo(request.getDeadline()),
                () -> assertThat(recruitBoard.getBoardPositions()).hasSize(2),
                () -> assertThat(recruitBoard.getBoardStacks()).hasSize(2)
        );
    }

    @DisplayName("모집 게시판 주인이 아닌자가 업데이트 시도 시 예외 발생")
    @Test
    void updateByNonOwner() {
        RecruitBoardRequest request = RecruitBoardRequest.builder()
                .title("모집 게시판 제목")
                .contents("모집합니다.")
                .recruitBoardType(RecruitBoardType.PROJECT)
                .progressType(ProgressType.ONLINE)
                .deadline(LocalDateTime.now())
                .expectedPeriod("3개월")
                .positions(List.of(new BoardPositionRequest(PositionType.BACKEND, 3)))
                .stacks(List.of(new BoardStackRequest(StackType.SPRING, StackLevelType.LOW)))
                .build();

        Long nonOwnerId = 2L;
        Long boardId = 1L;

        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));

        assertThatThrownBy(() -> recruitBoardService.updateRecruitBoard(nonOwnerId, boardId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("모집 게시판을 삭제한다.")
    @Test
    void deleteRecruitBoard() {
        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));

        recruitBoardService.deleteRecruitBoard(1L, 1L);

        verify(recruitBoardRepository).delete(any());
    }

    @DisplayName("모집 게시판 주인이 아닌자가 삭제 시도 시 예외 발생")
    @Test
    void deleteByNonOwner() {
        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));

        Long nonOwnerId = 2L;
        Long boardId = 1L;

        assertThatThrownBy(() -> recruitBoardService.deleteRecruitBoard(nonOwnerId, boardId))
                .isInstanceOf(IllegalArgumentException.class);
    }

}