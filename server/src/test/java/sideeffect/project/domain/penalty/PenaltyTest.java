package sideeffect.project.domain.penalty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;

class PenaltyTest {

    private RecruitBoard recruitBoard;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .email("hello@naver.com")
            .nickname("tester")
            .password("1234")
            .build();

        recruitBoard = RecruitBoard.builder()
            .id(1L)
            .contents("내용")
            .title("제목")
            .build();
    }

    @DisplayName("모집게시판에 대한 패널티를 부과한다.")
    @Test
    void penalize() {
        Penalty penalty = Penalty.penalize(user, recruitBoard);

        assertAll(
            () -> assertThat(penalty.getUser()).isEqualTo(user),
            () -> assertThat(penalty.getRecruitBoard()).isEqualTo(recruitBoard),
            () -> assertThat(user.getPenalties()).containsExactly(penalty),
            () -> assertThat(recruitBoard.getPenalties()).containsExactly(penalty)
        );
    }
}
