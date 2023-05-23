package sideeffect.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sideeffect.project.domain.like.RecruitLike;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.like.LikeResult;
import sideeffect.project.dto.like.RecruitLikeResponse;
import sideeffect.project.repository.RecruitBoardRepository;
import sideeffect.project.repository.RecruitLikeRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecruitLikeServiceTest {

    @InjectMocks
    private RecruitLikeService recruitLikeService;

    @Mock
    RecruitLikeRepository recruitLikeRepository;

    @Mock
    RecruitBoardRepository recruitBoardRepository;

    private User user;
    private RecruitBoard recruitBoard;
    private RecruitLike recruitLike;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@naver.com")
                .nickname("test")
                .password("1234")
                .build();

        recruitBoard = RecruitBoard.builder()
                .id(1L)
                .title("모집 게시판 제목")
                .projectName("프로젝트 명")
                .contents("모집 게시판 내용")
                .build();

        recruitLike = RecruitLike.createRecruitLike(user, recruitBoard);
    }

    @DisplayName("유저가 모집게시판을 추천한다.")
    @Test
    void recruitBoardLike() {
        when(recruitLikeRepository.findByUserIdAndRecruitBoardId(any(), any())).thenReturn(Optional.empty());
        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));
        when(recruitLikeRepository.save(any())).thenReturn(recruitLike);

        RecruitLikeResponse response = recruitLikeService.toggleLike(user, recruitBoard.getId());

        assertAll(
                () -> verify(recruitLikeRepository).findByUserIdAndRecruitBoardId(any(), any()),
                () -> verify(recruitBoardRepository).findById(any()),
                () -> verify(recruitLikeRepository).save(any()),
                () -> assertThat(response.getMessage()).isEqualTo(LikeResult.LIKE.getMessage()),
                () -> assertThat(recruitBoard.getRecruitLikes()).contains(recruitLike)
        );
    }

    @DisplayName("유저가 모집게시판 추천을 취소한다.")
    @Test
    void cancelLike() {
        when(recruitLikeRepository.findByUserIdAndRecruitBoardId(any(), any())).thenReturn(Optional.of(recruitLike));

        RecruitLikeResponse response = recruitLikeService.toggleLike(user, recruitBoard.getId());

        assertAll(
                () -> verify(recruitLikeRepository).findByUserIdAndRecruitBoardId(any(), any()),
                () -> assertThat(response.getMessage()).isEqualTo(LikeResult.CANCEL_LIKE.getMessage())
        );
    }

}
