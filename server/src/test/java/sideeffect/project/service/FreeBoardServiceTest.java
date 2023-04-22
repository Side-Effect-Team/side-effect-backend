package sideeffect.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import sideeffect.project.dto.freeboard.FreeBoardRequest;
import sideeffect.project.repository.FreeBoardRepository;

@ExtendWith(MockitoExtension.class)
class FreeBoardServiceTest {

    private FreeBoardService freeBoardService;

    @Mock
    private FreeBoardRepository freeBoardRepository;

    private FreeBoard freeBoard;

    @BeforeEach
    void setUp() {
        freeBoardService = new FreeBoardService(freeBoardRepository);
        freeBoard = FreeBoard.builder()
            .id(1L)
            .title("자랑 게시판")
            .content("제가 만든 겁니다.")
            .projectUrl("url")
            .userId(1L)
            .build();
    }

    @DisplayName("게시판을 저장한다.")
    @Test
    void register() {
        FreeBoardRequest request = FreeBoardRequest.builder()
            .title("자랑 게시판").content("제가 만든 겁니다.").projectUrl("url").build();
        Long userId = 1L;

        freeBoardService.register(userId, request);

        verify(freeBoardRepository).save(any());
    }

    @DisplayName("게시판을 업데이트한다.")
    @Test
    void updateBoard() {
        FreeBoardRequest request = FreeBoardRequest.builder()
            .title("자랑 게시판").content("제가 만든 겁니다.").projectUrl("url").build();
        Long userId = 1L;
        Long boardId = 1L;
        when(freeBoardRepository.findById(any())).thenReturn(Optional.of(freeBoard));

        freeBoardService.updateBoard(userId, boardId, request);

        assertAll(
            () -> verify(freeBoardRepository).findById(any()),
            () -> assertThat(freeBoard.getTitle()).isEqualTo(request.getTitle()),
            () -> assertThat(freeBoard.getContent()).isEqualTo(request.getContent()),
            () -> assertThat(freeBoard.getProjectUrl()).isEqualTo(request.getProjectUrl())
        );
    }

    @DisplayName("게시판 주인이 아닌자가 업데이트를 수행시 예외가 발생")
    @Test
    void updateByNonOwner() {
        FreeBoardRequest request = FreeBoardRequest.builder()
            .title("자랑 게시판").content("제가 만든 겁니다.").projectUrl("url").build();
        Long nonOwnerId = 2L;
        Long boardId = 1L;
        when(freeBoardRepository.findById(any())).thenReturn(Optional.of(freeBoard));

        assertThatThrownBy(() -> freeBoardService.updateBoard(nonOwnerId, boardId, request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("게시판을 단건 조회한다.")
    @Test
    void findBoard() {
        when(freeBoardRepository.findById(any())).thenReturn(Optional.of(freeBoard));

        freeBoardService.findBoard(1L);

        verify(freeBoardRepository).findById(any());
    }

    @DisplayName("게시판을 삭제한다.")
    @Test
    void deleteBoard() {
        when(freeBoardRepository.findById(any())).thenReturn(Optional.of(freeBoard));

        freeBoardService.deleteBoard(1L, 1L);

        verify(freeBoardRepository).delete(any());
    }

    @DisplayName("게시판 주인이 아닌자가 삭제 시 예외가 발생")
    @Test
    void deleteByNonOwner() {
        Long nonOwnerId = 2L;
        when(freeBoardRepository.findById(any())).thenReturn(Optional.of(freeBoard));

        assertThatThrownBy(() -> freeBoardService.deleteBoard(nonOwnerId, 1L))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
