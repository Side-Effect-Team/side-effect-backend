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
import sideeffect.project.domain.recommend.Recommend;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.recommend.RecommendResult;
import sideeffect.project.dto.recommend.RecommendRequest;
import sideeffect.project.dto.recommend.RecommendResponse;
import sideeffect.project.repository.FreeBoardRepository;
import sideeffect.project.repository.RecommendRepository;
import sideeffect.project.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RecommendServiceTest {

    private RecommendService recommendService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FreeBoardRepository freeBoardRepository;

    @Mock
    private RecommendRepository recommendRepository;

    private User user;
    private FreeBoard freeBoard;
    private Recommend recommend;

    @BeforeEach
    void setUp() {
        recommendService = new RecommendService(recommendRepository, userRepository, freeBoardRepository);

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

        recommend = Recommend.recommend(user, freeBoard);
    }

    @DisplayName("유저가 게시판을 추천한다.")
    @Test
    void recommendBoard() {
        when(recommendRepository.findByUserIdAndFreeBoardId(any(), any())).thenReturn(Optional.empty());
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(freeBoardRepository.findById(any())).thenReturn(Optional.of(freeBoard));
        when(recommendRepository.save(any())).thenReturn(recommend);
        RecommendRequest request = new RecommendRequest(user.getId(), freeBoard.getId());

        RecommendResponse response = recommendService.toggleRecommend(request);

        assertAll(
            () -> verify(recommendRepository).findByUserIdAndFreeBoardId(any(), any()),
            () -> verify(userRepository).findById(any()),
            () -> verify(freeBoardRepository).findById(any()),
            () -> verify(recommendRepository).save(any()),
            () -> assertThat(response.getMessage()).isEqualTo(RecommendResult.RECOMMEND.getMessage())
        );
    }

    @DisplayName("유저가 게시판 추천을 취소한다.")
    @Test
    void cancelRecommend() {
        when(recommendRepository.findByUserIdAndFreeBoardId(any(), any())).thenReturn(Optional.of(recommend));
        RecommendRequest request = new RecommendRequest(user.getId(), freeBoard.getId());

        RecommendResponse response = recommendService.toggleRecommend(request);

        assertAll(
            () -> verify(recommendRepository).findByUserIdAndFreeBoardId(any(), any()),
            () -> assertThat(response.getMessage()).isEqualTo(RecommendResult.CANCEL_RECOMMEND.getMessage()),
            () -> assertThat(freeBoard.getRecommends()).doesNotContain(recommend)
        );
    }
}
