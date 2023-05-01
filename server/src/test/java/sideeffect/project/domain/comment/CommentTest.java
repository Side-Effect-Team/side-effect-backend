package sideeffect.project.domain.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;

class CommentTest {

    private User user;
    private FreeBoard freeBoard;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .nickname("tester")
            .password("1234")
            .userRoleType(UserRoleType.ROLE_USER)
            .email("test@naver.com")
            .build();

        freeBoard = FreeBoard.builder()
            .id(1L)
            .title("게시판")
            .imgUrl("/test.jpg")
            .projectUrl("http://test.url")
            .content("게시판 입니다.")
            .build();

        comment = new Comment("댓글 입니다.");
    }

    @DisplayName("댓글을 업데이트 한다.")
    @Test
    void update() {
        String updatedContent = "업데이트";
        comment.update(updatedContent);

        assertThat(comment.getContent()).isEqualTo(updatedContent);
    }

    @DisplayName("유저와 게시판에 연관짓는다.")
    @Test
    void associate() {
        comment.associate(user, freeBoard);

        assertAll(
            () -> assertThat(comment.getFreeBoard()).isEqualTo(freeBoard),
            () -> assertThat(comment.getUser()).isEqualTo(user),
            () -> assertThat(user.getComments()).contains(comment),
            () -> assertThat(freeBoard.getComments()).contains(comment)
        );
    }
}
