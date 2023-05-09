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
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.ProgressType;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.recruit.RecruitBoardType;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.recruit.*;
import sideeffect.project.repository.RecruitBoardRepository;
import sideeffect.project.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
                .tags(List.of(StackType.SPRING))
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
                .tags(List.of(
                        StackType.SPRING,
                        StackType.JAVA
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
                .tags(List.of(StackType.SPRING))
                .build();

        Long nonOwnerId = 2L;
        Long boardId = 1L;

        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));

        assertThatThrownBy(() -> recruitBoardService.updateRecruitBoard(nonOwnerId, boardId, request))
                .isInstanceOf(AuthException.class);
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
                .isInstanceOf(AuthException.class);
    }

    @DisplayName("모집 게시판 목록을 스크롤 조회한다.")
    @MethodSource("generateScrollTestAugments")
    @ParameterizedTest
    void findBoardsWithLastId(RecruitBoardScrollRequest request, List<RecruitBoard> recruitBoards, boolean hasNext) {
        when(recruitBoardRepository.findWithSearchConditions(any(), any(), any(), any())).thenReturn(recruitBoards);

        RecruitBoardScrollResponse scrollResponse = recruitBoardService.findRecruitBoards(request);

        assertAll(
                () -> verify(recruitBoardRepository).findWithSearchConditions(any(), any(), any(), any()),
                () -> assertThat(scrollResponse.getLastId()).isEqualTo(recruitBoards.get(recruitBoards.size() - 1).getId()),
                () -> assertThat(scrollResponse.isHasNext()).isEqualTo(hasNext)
        );
    }

    @DisplayName("모집 게시판 목록을 키워드로 검색한다.")
    @Test
    void findBoardWithKeyword() {
        String searchContents = "검색할 컨텐츠";
        RecruitBoard recruitBoard1 = RecruitBoard.builder().id(10L).recruitBoardType(RecruitBoardType.PROJECT).progressType(ProgressType.ONLINE).title("모집 게시판" + searchContents).contents("!@#$%").build();
        RecruitBoard recruitBoard2 = RecruitBoard.builder().id(1L).recruitBoardType(RecruitBoardType.PROJECT).progressType(ProgressType.ONLINE).title("모집 게시판").contents("!@#$%" + searchContents).build();
        recruitBoard1.associateUser(user);
        recruitBoard2.associateUser(user);
        RecruitBoardScrollRequest request = RecruitBoardScrollRequest.builder().keyword(searchContents).size(2).build();

        when(recruitBoardRepository.findWithSearchConditions(any(), any(), any(), any())).thenReturn(List.of(recruitBoard1, recruitBoard2));

        RecruitBoardScrollResponse scrollResponse = recruitBoardService.findRecruitBoards(request);

        assertAll(
                () -> verify(recruitBoardRepository).findWithSearchConditions(any(), any(), any(), any()),
                () -> assertThat(scrollResponse.getLastId()).isEqualTo(1L),
                () -> assertThat(scrollResponse.isHasNext()).isFalse()
        );

    }

    private static Stream<Arguments> generateScrollTestAugments() {
        return Stream.of(
                Arguments.arguments(RecruitBoardScrollRequest.builder().lastId(100L).size(10).build(),
                        generateRecruitBoards(1L, 11), true),
                Arguments.arguments(RecruitBoardScrollRequest.builder().lastId(100L).size(11).build(),
                        generateRecruitBoards(1L, 11), false),
                Arguments.arguments(RecruitBoardScrollRequest.builder().lastId(5L).size(1).build(),
                        generateRecruitBoards(1L, 10), true),
                Arguments.arguments(RecruitBoardScrollRequest.builder().lastId(1L).size(10).build(),
                        generateRecruitBoards(1L, 10), false),
                Arguments.arguments(RecruitBoardScrollRequest.builder().size(10).build(),
                        generateRecruitBoards(1L, 11), true),
                Arguments.arguments(RecruitBoardScrollRequest.builder().size(10).build(),
                        generateRecruitBoards(1L, 10), false),
                Arguments.arguments(RecruitBoardScrollRequest.builder().size(1).build(),
                        generateRecruitBoards(1L, 10), true)
        );
    }

    private static List<RecruitBoard> generateRecruitBoards(Long startId, int size) {
        User owner = User.builder().id(1L).email("test1234@naver.com").password("qwer1234!").build();
        List<RecruitBoard> recruitBoards = new ArrayList<>();
        for (Long i = startId; i < startId + size; i++) {
            RecruitBoard recruitBoard = RecruitBoard.builder().id(i).title("모집 게시판" + i).recruitBoardType(RecruitBoardType.PROJECT).progressType(ProgressType.ONLINE).contents("모집합니다." + i).build();
            recruitBoard.associateUser(owner);
            recruitBoards.add(recruitBoard);
        }
        return recruitBoards;
    }
}