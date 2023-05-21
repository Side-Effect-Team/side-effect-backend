package sideeffect.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.penalty.Penalty;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.repository.BoardPositionRepository;
import sideeffect.project.repository.PenaltyRepository;

@ExtendWith(MockitoExtension.class)
class PenaltyServiceTest {

    private PenaltyService penaltyService;

    @Mock
    private PenaltyRepository penaltyRepository;

    private User user;
    private RecruitBoard recruitBoard;
    private BoardPosition boardPosition;
    private Applicant applicant;

    @BeforeEach
    void setUp() {
        penaltyService = new PenaltyService(penaltyRepository);

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

        boardPosition = BoardPosition.builder()
            .id(1L)
            .targetNumber(3)
            .recruitBoard(recruitBoard)
            .build();

        applicant = new Applicant(1L);
        applicant.associate(user, boardPosition);

    }

    @DisplayName("패널티를 부과한다.")
    @Test
    void penalize() {
        penaltyService.penalize(user, applicant);

        assertAll(
            () -> verify(penaltyRepository).save(any())
        );
    }

    @DisplayName("해당 유저가 모집 게시판에 대해 패널티가 부과되었다.")
    @Test
    void isPenalized() {
        Penalty penalty = Penalty.builder().user(user).recruitBoard(recruitBoard)
            .createdAt(LocalDateTime.now()).build();
        when(penaltyRepository.findByUserIdAndRecruitBoardId(any(), any())).thenReturn(Optional.of(penalty));

        boolean result = penaltyService.isPenalized(user, recruitBoard);

        assertAll(
            () -> verify(penaltyRepository).findByUserIdAndRecruitBoardId(any(), any()),
            () -> assertThat(result).isTrue()
        );
    }

    @DisplayName("해당 유저가 모집 게시판에 대해 패널티가 부과되지 않았다.")
    @Test
    void isNotPenalized() {
        when(penaltyRepository.findByUserIdAndRecruitBoardId(any(), any())).thenReturn(Optional.empty());

        boolean result = penaltyService.isPenalized(user, recruitBoard);

        assertAll(
            () -> verify(penaltyRepository).findByUserIdAndRecruitBoardId(any(), any()),
            () -> assertThat(result).isFalse()
        );
    }

    @DisplayName("패널티가 만료되었다.")
    @Test
    void isExpired() {
        Penalty penalty = Penalty.builder().user(user).recruitBoard(recruitBoard)
            .createdAt(LocalDateTime.now().minusDays(2)).build();
        when(penaltyRepository.findByUserIdAndRecruitBoardId(any(), any())).thenReturn(Optional.of(penalty));

        boolean result = penaltyService.isPenalized(user, recruitBoard);

        assertAll(
            () -> verify(penaltyRepository).findByUserIdAndRecruitBoardId(any(), any()),
            () -> verify(penaltyRepository).delete(any()),
            () -> assertThat(result).isFalse()
        );
    }
}
