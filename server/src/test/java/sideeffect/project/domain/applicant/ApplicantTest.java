package sideeffect.project.domain.applicant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ApplicantTest {

    private User user;
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

        boardPosition = BoardPosition.builder()
                .id(1L)
                .targetNumber(3)
                .build();

        applicant = Applicant.builder()
                .id(1L)
                .build();
    }

    @DisplayName("유저와 게시판 포지션에 연관관계를 맺는다.")
    @Test
    void associate() {
        applicant.associate(user, boardPosition);

        assertAll(
                () -> assertThat(applicant.getUser()).isEqualTo(user),
                () -> assertThat(applicant.getBoardPosition()).isEqualTo(boardPosition)
        );
    }

}