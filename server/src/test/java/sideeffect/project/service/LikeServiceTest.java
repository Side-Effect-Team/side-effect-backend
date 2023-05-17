package sideeffect.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.like.Like;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.like.LikeResult;
import sideeffect.project.dto.like.LikeResponse;
import sideeffect.project.repository.FreeBoardRepository;
import sideeffect.project.repository.LikeRepository;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    private LikeService likeService;

    @Mock
    private FreeBoardRepository freeBoardRepository;

    @Mock
    private LikeRepository likeRepository;

    private User user;
    private FreeBoard freeBoard;
    private Like like;

    @BeforeEach
    void setUp() {
        likeService = new LikeService(likeRepository, freeBoardRepository);

        user = User.builder()
            .id(1L)
            .email("test@naver.com")
            .nickname("tester")
            .password("1234")
            .build();

        freeBoard = FreeBoard.builder()
            .id(1L)
            .title("게시판입니다.")
            .projectUrl("url")
            .imgUrl("/test.jpg")
            .content("test")
            .build();

        like = Like.like(user, freeBoard);
    }

    @DisplayName("유저가 게시판을 추천한다.")
    @Test
    void likeBoard() {
        when(likeRepository.searchLike(any(), any())).thenReturn(Optional.empty());
        when(freeBoardRepository.findById(any())).thenReturn(Optional.of(freeBoard));
        when(likeRepository.save(any())).thenReturn(like);

        LikeResponse response = likeService.toggleLike(user, freeBoard.getId());

        assertAll(
            () -> verify(likeRepository).searchLike(any(), any()),
            () -> verify(freeBoardRepository).findById(any()),
            () -> verify(likeRepository).save(any()),
            () -> assertThat(response.getMessage()).isEqualTo(LikeResult.LIKE.getMessage())
        );
    }

    @DisplayName("유저가 게시판 추천을 취소한다.")
    @Test
    void cancelLike() {
        when(likeRepository.searchLike(any(), any())).thenReturn(Optional.of(like));

        LikeResponse response = likeService.toggleLike(user, freeBoard.getId());

        assertAll(
            () -> verify(likeRepository).searchLike(any(), any()),
            () -> assertThat(response.getMessage()).isEqualTo(LikeResult.CANCEL_LIKE.getMessage()),
            () -> verify(likeRepository).delete(any())
        );
    }
}
