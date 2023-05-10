package sideeffect.project.domain.freeboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assumptions.assumingThat;

class FreeBoardTest {

    private FreeBoard freeBoard;
    private User user;

    @BeforeEach
    void setUp() {
        freeBoard = FreeBoard.builder()
            .id(1L)
            .title("게시판")
            .imgUrl("/test.jpg")
            .projectUrl("http://test.url")
            .content("게시판 입니다.")
            .build();

        user = User.builder()
            .id(1L)
            .nickname("tester")
            .password("1234")
            .userRoleType(UserRoleType.ROLE_USER)
            .email("test@naver.com")
            .build();
    }

    @DisplayName("게시판을 업데이트 한다.")
    @MethodSource("generateUpdateBoards")
    @ParameterizedTest
    void update(FreeBoard updateBoard) {
        freeBoard.update(updateBoard);

        assertAll(
            () -> assumingThat(updateBoard.getProjectUrl() != null,
                () -> assertThat(freeBoard.getProjectUrl()).isEqualTo(updateBoard.getProjectUrl())),
            () -> assumingThat(updateBoard.getContent() != null,
                () -> assertThat(updateBoard.getContent()).isEqualTo(freeBoard.getContent())),
            () -> assumingThat(updateBoard.getTitle() != null,
                () -> assertThat(updateBoard.getTitle()).isEqualTo(freeBoard.getTitle())),
            () -> assumingThat(updateBoard.getProjectName() != null,
                () -> assertThat(updateBoard.getProjectName()).isEqualTo(freeBoard.getProjectName()))
            );
    }

    @DisplayName("조회수가 증가한다.")
    @Test
    void increaseViews() {
        int beforeViews = freeBoard.getViews();

        freeBoard.increaseViews();

        assertThat(freeBoard.getViews()).isEqualTo(beforeViews + 1);
    }

    @DisplayName("이미지 url를 변경한다.")
    @Test
    void changeImageUrl() {
        String updateUrl = "/update.jpg";

        freeBoard.changeImageUrl(updateUrl);

        assertThat(freeBoard.getImgUrl()).isEqualTo(updateUrl);
    }

    @DisplayName("이미지 url을 삭제한다.")
    @Test
    void deleteImageUrl() {
        freeBoard.deleteImageUrl();

        assertThat(freeBoard.getImgUrl()).isNull();
    }

    @DisplayName("유저를 설정한다.")
    @Test
    void associateUser() {
        freeBoard.associateUser(user);

        assertAll(
            () -> assertThat(freeBoard.getUser()).isEqualTo(user),
            () -> assertThat(user.getFreeBoards()).contains(freeBoard)
        );
    }

    @DisplayName("유저를 재설정 한다.")
    @Test
    void updateUser() {
        User newUser = User.builder()
            .id(2L)
            .password("1234")
            .nickname("newUser")
            .build();
        freeBoard.associateUser(user);
        freeBoard.associateUser(newUser);

        assertAll(
            () -> assertThat(freeBoard.getUser()).isEqualTo(newUser),
            () -> assertThat(newUser.getFreeBoards()).contains(freeBoard),
            () -> assertThat(user.getFreeBoards()).doesNotContain(freeBoard)
        );
    }


    private static Stream<Arguments> generateUpdateBoards() {
        return Stream.of(
            Arguments.arguments(FreeBoard.builder().content("변경").projectUrl("변경 url").title("변경 제목").build()),
            Arguments.arguments(FreeBoard.builder().content("변경").projectUrl("변경 url").build()),
            Arguments.arguments(FreeBoard.builder().content("변경").title("변경 제목").projectName("강아지 앱").build()),
            Arguments.arguments(FreeBoard.builder().projectUrl("변경 url").title("변경 제목").build()),
            Arguments.arguments(FreeBoard.builder().content("변경").title("변경 제목").build()));
    }
}
