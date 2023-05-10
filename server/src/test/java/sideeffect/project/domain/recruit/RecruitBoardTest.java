package sideeffect.project.domain.recruit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RecruitBoardTest {

    private User user;
    private RecruitBoard recruitBoard;

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
    }

    @DisplayName("게시판의 포지션을 변경한다.")
    @Test
    void updateBoardPositions() {
        List<BoardPosition> boardPositions = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            boardPositions.add(BoardPosition.builder()
                    .targetNumber(3)
                    .build());
        }

        recruitBoard.updateBoardPositions(boardPositions);

        assertThat(recruitBoard.getBoardPositions()).hasSize(3);
    }

    @DisplayName("게시판의 스택을 변경한다.")
    @Test
    void updateBoardStacks() {
        List<BoardStack> boardStacks = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            boardStacks.add(BoardStack.builder().build());
        }

        recruitBoard.updateBoardStacks(boardStacks);

        assertThat(recruitBoard.getBoardStacks()).hasSize(3);
    }

    @DisplayName("모집 게시판을 업데이트 한다.")
    @MethodSource("generateUpdateBoards")
    @ParameterizedTest
    void update(RecruitBoard updateRecruitBoard) {
        recruitBoard.update(updateRecruitBoard);

        assertAll(
                () -> {
                    if (updateRecruitBoard.getTitle() != null) {
                        assertThat(recruitBoard.getTitle()).isEqualTo(updateRecruitBoard.getTitle());
                    }
                },
                () -> {
                    if (updateRecruitBoard.getContents() != null) {
                        assertThat(recruitBoard.getContents()).isEqualTo(updateRecruitBoard.getContents());
                    }
                });
    }

    @DisplayName("게시판 조회수가 증가한다.")
    @Test
    void increaseViews() {
        int beforeViews = recruitBoard.getViews();

        recruitBoard.increaseViews();

        assertThat(recruitBoard.getViews()).isEqualTo(beforeViews + 1);
    }

    @DisplayName("게시판의 유저를 설정한다.")
    @Test
    void associateUser() {
        recruitBoard.associateUser(user);

        assertAll(
                () -> assertThat(recruitBoard.getUser()).isEqualTo(user),
                () -> assertThat(user.getRecruitBoards()).contains(recruitBoard)
        );
    }

    private static Stream<Arguments> generateUpdateBoards() {
        return Stream.of(
                Arguments.arguments(RecruitBoard.builder().title("변경").build()),
                Arguments.arguments(RecruitBoard.builder().title("변경2").contents("내용 변경2").build()));
    }

}