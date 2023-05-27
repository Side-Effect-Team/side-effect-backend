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
import sideeffect.project.common.exception.InvalidValueException;
import sideeffect.project.common.fileupload.service.RecruitUploadService;
import sideeffect.project.domain.like.RecruitLike;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.recruit.*;
import sideeffect.project.repository.RecruitBoardRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private PositionService positionService;

    @Mock
    private StackService stackService;

    @Mock
    private RecruitUploadService recruitUploadService;

    @Mock
    private MailService mailService;

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
    void register() throws IOException {
        String imgPath = "/test/test.png";
        RecruitBoardRequest request = RecruitBoardRequest.builder()
                .title("모집 게시판 제목")
                .content("모집합니다.")
                .positions(List.of(new BoardPositionRequest(PositionType.BACKEND, 3)))
                .tags(List.of(StackType.SPRING))
                .build();

        Long userId = 1L;

        when(recruitBoardRepository.save(any())).thenReturn(recruitBoard);
        when(positionService.findByPositionType(any())).thenReturn(position);
        when(stackService.findByStackType(any())).thenReturn(stack);
        when(recruitUploadService.storeFile(any())).thenReturn(imgPath);

        recruitBoardService.register(user, request);

        assertAll(
                () -> verify(recruitBoardRepository).save(any()),
                () -> verify(recruitUploadService).storeFile(any())
        );

    }

    @DisplayName("모집 게시판을 조회한다.")
    @Test
    void getRecruitBoard() {
        int beforeViews = recruitBoard.getViews();
        RecruitBoardAndLikeDto likeDto = RecruitBoardAndLikeDto.builder().recruitBoard(recruitBoard).build();

        when(recruitBoardRepository.findByBoardIdAndUserId(any(),any())).thenReturn(Optional.of(likeDto));

        recruitBoardService.findRecruitBoard(1L, user);

        assertAll(
                () -> verify(recruitBoardRepository).findByBoardIdAndUserId(any(), any()),
                () -> assertThat(recruitBoard.getViews()).isEqualTo(beforeViews + 1)
        );
    }

    @DisplayName("모집 게시판 상세 조회에 좋아요 여부도 반환한다.")
    @Test
    void getRecruitBoardWithLike() {
        int beforeViews = recruitBoard.getViews();
        RecruitLike recruitLike = RecruitLike.createRecruitLike(user, recruitBoard);

        RecruitBoardAndLikeDto likeDto = RecruitBoardAndLikeDto.builder().recruitBoard(recruitBoard).like(true).build();

        when(recruitBoardRepository.findByBoardIdAndUserId(any(),any())).thenReturn(Optional.of(likeDto));

        DetailedRecruitBoardResponse response = recruitBoardService.findRecruitBoard(recruitBoard.getId(), user);

        assertAll(
                () -> verify(recruitBoardRepository).findByBoardIdAndUserId(any(), any()),
                () -> assertThat(recruitBoard.getViews()).isEqualTo(beforeViews + 1),
                () -> assertThat(response.isLike()).isTrue(),
                () -> assertThat(response.getLikeNum()).isEqualTo(1)
        );
    }

    @DisplayName("모집 게시판을 전체 조회한다.")
    @Test
    void findAllRecruitBoard() {
        List<RecruitBoardAndLikeDto> recruitBoards = generateRecruitBoards(1L, 100);

        when(recruitBoardRepository.findByAllWithLike(any())).thenReturn(recruitBoards);

        RecruitBoardAllResponse allRecruitBoard = recruitBoardService.findAllRecruitBoard(user);

        assertAll(
                () -> verify(recruitBoardRepository).findByAllWithLike(any()),
                () -> assertThat(allRecruitBoard.getRecruitBoards()).hasSize(100)
        );
    }

    @DisplayName("모집 게시판을 업데이트한다.")
    @Test
    void updateRecruitBoard() {
        String imgPath = "/test/test.png";
        RecruitBoardUpdateRequest request = RecruitBoardUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
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
                () -> assertThat(recruitBoard.getContents()).isEqualTo(request.getContent()),
                () -> assertThat(recruitBoard.getBoardPositions()).hasSize(0),
                () -> assertThat(recruitBoard.getBoardStacks()).hasSize(2)
        );
    }

    @DisplayName("모집 게시판 주인이 아닌자가 업데이트 시도 시 예외 발생")
    @Test
    void updateByNonOwner() {
        RecruitBoardUpdateRequest request = RecruitBoardUpdateRequest.builder()
                .title("모집 게시판 제목")
                .content("모집합니다.")
                .tags(List.of(StackType.SPRING))
                .build();

        Long nonOwnerId = 2L;
        Long boardId = 1L;

        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));

        assertThatThrownBy(() -> recruitBoardService.updateRecruitBoard(nonOwnerId, boardId, request))
                .isInstanceOf(AuthException.class);
    }

    @DisplayName("모집 게시판에 포지션을 추가한다.")
    @Test
    public void addRecruitBoardPosition() {
        BoardPositionRequest request = BoardPositionRequest.builder().positionType(PositionType.BACKEND).targetNumber(3).build();

        Long boardId = 1L;
        Long userId = 1L;
        int beforeSize = recruitBoard.getBoardPositions().size();

        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));

        recruitBoardService.addRecruitBoardPosition(userId, boardId, request);

        assertAll(
                () -> verify(recruitBoardRepository).findById(any()),
                () -> assertThat(recruitBoard.getBoardPositions()).hasSize(beforeSize + 1)
        );
    }

    @DisplayName("모집 게시판 주인이 아닌자가 포지션을 추가하려고 할 시 예외 발생")
    @Test
    void addRecruitBoardPositionNonOwner() {
        BoardPositionRequest request = BoardPositionRequest.builder().positionType(PositionType.BACKEND).targetNumber(3).build();

        Long nonOwnerId = 2L;
        Long boardId = 1L;

        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));

        assertThatThrownBy(() -> recruitBoardService.addRecruitBoardPosition(nonOwnerId, boardId, request))
                .isInstanceOf(AuthException.class);
    }

    @DisplayName("모집 게시판에 이미 존재하는 포지션을 추가하려고 한다면 예외 발생")
    @Test
    void addRecruitBoardPositionExists() {
        BoardPositionRequest request = BoardPositionRequest.builder().positionType(PositionType.BACKEND).targetNumber(3).build();
        Position backEndPosition = Position.builder().id(1L).positionType(PositionType.BACKEND).build();
        BoardPosition boardPositionBack = BoardPosition.builder().position(backEndPosition).targetNumber(3).build();
        recruitBoard.addBoardPosition(boardPositionBack);

        Long boardId = 1L;
        Long userId = 1L;

        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));
        when(positionService.findByPositionType(any())).thenReturn(position);

        assertThatThrownBy(() -> recruitBoardService.addRecruitBoardPosition(userId, boardId, request))
                .isInstanceOf(InvalidValueException.class);
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
    void findBoardsWithLastId(RecruitBoardScrollRequest request, List<RecruitBoardAndLikeDto> recruitBoards, boolean hasNext) {
        when(recruitBoardRepository.findWithSearchConditions(any(), any(), any(), any(), any())).thenReturn(recruitBoards);

        RecruitBoardScrollResponse scrollResponse = recruitBoardService.findRecruitBoards(request, user);
        List<RecruitBoard> findRecruitBoardsOfList = recruitBoards.stream().map(RecruitBoardAndLikeDto::getRecruitBoard).collect(Collectors.toList());

        assertAll(
                () -> verify(recruitBoardRepository).findWithSearchConditions(any(), any(), any(), any(), any()),
                () -> assertThat(scrollResponse.getLastId()).isEqualTo(findRecruitBoardsOfList.get(recruitBoards.size() - 1).getId()),
                () -> assertThat(scrollResponse.isHasNext()).isEqualTo(hasNext)
        );
    }

    @DisplayName("모집 게시판 목록을 키워드로 검색한다.")
    @Test
    void findBoardWithKeyword() {
        String searchContents = "검색할 컨텐츠";
        RecruitBoard recruitBoard1 = RecruitBoard.builder().id(10L).title("모집 게시판" + searchContents).contents("!@#$%").build();
        RecruitBoardAndLikeDto likeDto1 = RecruitBoardAndLikeDto.builder().recruitBoard(recruitBoard1).build();
        RecruitBoard recruitBoard2 = RecruitBoard.builder().id(1L).title("모집 게시판").contents("!@#$%" + searchContents).build();
        RecruitBoardAndLikeDto likeDto2 = RecruitBoardAndLikeDto.builder().recruitBoard(recruitBoard2).build();
        recruitBoard1.associateUser(user);
        recruitBoard2.associateUser(user);
        RecruitBoardScrollRequest request = RecruitBoardScrollRequest.builder().keyword(searchContents).size(2).build();

        when(recruitBoardRepository.findWithSearchConditions(any(), any(), any(), any(), any())).thenReturn(List.of(likeDto1, likeDto2));

        RecruitBoardScrollResponse scrollResponse = recruitBoardService.findRecruitBoards(request, user);

        assertAll(
                () -> verify(recruitBoardRepository).findWithSearchConditions(any(), any(), any(), any(), any()),
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

    private static List<RecruitBoardAndLikeDto> generateRecruitBoards(Long startId, int size) {
        User owner = User.builder().id(1L).email("test1234@naver.com").password("qwer1234!").build();
        List<RecruitBoardAndLikeDto> recruitBoards = new ArrayList<>();
        for (Long i = startId; i < startId + size; i++) {
            RecruitBoard recruitBoard = RecruitBoard.builder().id(i).title("모집 게시판" + i).contents("모집합니다." + i).build();
            recruitBoard.associateUser(owner);
            RecruitBoardAndLikeDto dto = RecruitBoardAndLikeDto.builder().recruitBoard(recruitBoard).build();
            recruitBoards.add(dto);
        }
        return recruitBoards;
    }
}
