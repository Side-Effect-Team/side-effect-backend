package sideeffect.project.domain.recommend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;

class RecommendTest {

    private User user;
    private FreeBoard freeBoard;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .email("hello@naver.com")
            .nickname("tester")
            .password("1234")
            .build();

        freeBoard = FreeBoard.builder()
            .id(1L)
            .content("게시판")
            .title("제목")
            .build();
    }

    @DisplayName("유저가 해당 게시판을 추천한다.")
    @Test
    void recommend() {
        Recommend recommend = Recommend.recommend(user, freeBoard);

        assertAll(
            () -> assertThat(user.getRecommends()).containsExactly(recommend),
            () -> assertThat(freeBoard.getRecommends()).containsExactly(recommend),
            () -> assertThat(recommend.getFreeBoard()).isEqualTo(freeBoard),
            () -> assertThat(recommend.getUser()).isEqualTo(user)
        );
    }
}
