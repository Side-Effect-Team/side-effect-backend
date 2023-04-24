package sideeffect.project.domain.freeboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FreeBoardTest {

    private FreeBoard freeBoard;

    @BeforeEach
    void setUp() {
        freeBoard = FreeBoard.builder()
            .id(1L)
            .title("게시판")
            .imgUrl("/test.jpg")
            .projectUrl("http://test.url")
            .content("게시판 입니다.")
            .build();
    }

    @DisplayName("게시판을 업데이트 한다.")
    @MethodSource("generateUpdateBoards")
    @ParameterizedTest
    void update(FreeBoard updateBoard) {
        freeBoard.update(updateBoard);

        assertAll(
            () -> {
                if (updateBoard.getProjectUrl() != null) {
                    assertThat(freeBoard.getProjectUrl()).isEqualTo(updateBoard.getProjectUrl());
                }
            },
            () -> {
                if (updateBoard.getContent() != null) {
                    assertThat(freeBoard.getContent()).isEqualTo(updateBoard.getContent());
                }
            },
            () -> {
                if (updateBoard.getTitle() != null) {
                    assertThat(freeBoard.getTitle()).isEqualTo(updateBoard.getTitle());
                }
            });
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
    void setUser() {
        Long userId = 1L;

        freeBoard.setUser(userId);

        assertThat(freeBoard.getUserId()).isEqualTo(userId);
    }

    private static Stream<Arguments> generateUpdateBoards() {
        return Stream.of(
            Arguments.arguments(FreeBoard.builder().content("변경").projectUrl("변경 url").title("변경 제목").build()),
            Arguments.arguments(FreeBoard.builder().content("변경").projectUrl("변경 url").build()),
            Arguments.arguments(FreeBoard.builder().content("변경").title("변경 제목").build()),
            Arguments.arguments(FreeBoard.builder().projectUrl("변경 url").title("변경 제목").build()));
    }
}
