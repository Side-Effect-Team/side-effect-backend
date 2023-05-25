package sideeffect.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.fileupload.service.FreeBoardUploadService;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.freeboard.FreeBoardKeyWordRequest;
import sideeffect.project.dto.freeboard.FreeBoardRequest;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollRequest;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.repository.FreeBoardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import sideeffect.project.repository.LikeRepository;

@ExtendWith(MockitoExtension.class)
class FreeBoardServiceTest {

    private FreeBoardService freeBoardService;

    @Mock
    private FreeBoardRepository freeBoardRepository;

    @Mock
    private FreeBoardUploadService freeBoardUploadService;

    @Mock
    private LikeRepository likeRepository;

    private FreeBoard freeBoard;
    private User user;

    @BeforeEach
    void setUp() {
        freeBoardService = new FreeBoardService(freeBoardRepository, freeBoardUploadService, likeRepository);

        user = User.builder()
            .id(1L)
            .nickname("tester")
            .password("1234")
            .userRoleType(UserRoleType.ROLE_USER)
            .email("test@naver.com")
            .build();

        freeBoard = FreeBoard.builder()
            .id(1L)
            .title("자랑 게시판")
            .content("제가 만든 겁니다.")
            .projectUrl("url")
            .build();
        freeBoard.associateUser(user);
    }

    @DisplayName("게시판을 저장한다.")
    @Test
    void register() {
        FreeBoardRequest request = FreeBoardRequest.builder()
            .title("자랑 게시판").content("제가 만든 겁니다.").projectUrl("url").build();

        freeBoardService.register(user, request);

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
            .isInstanceOf(AuthException.class);
    }

    @DisplayName("게시판을 단건 조회한다.")
    @Test
    void findBoard() {
        int beforeViews = freeBoard.getViews();
        when(freeBoardRepository.searchBoardFetchJoin(any())).thenReturn(Optional.of(freeBoard));

        freeBoardService.findBoard(1L, null);

        assertAll(
            () -> verify(freeBoardRepository).searchBoardFetchJoin(any()),
            () -> assertThat(freeBoard.getViews()).isEqualTo(beforeViews + 1)
        );
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
            .isInstanceOf(AuthException.class);
    }

    @DisplayName("게시판을 스크롤 조회한다.")
    @MethodSource("generateScrollTestAugments")
    @ParameterizedTest
    void findBoardByScroll(FreeBoardScrollRequest request, List<FreeBoard> freeBoards, boolean hasNext) {
        when(freeBoardRepository.searchScroll(any(), any())).thenReturn(FreeBoardResponse.listOf(freeBoards));

        FreeBoardScrollResponse response = freeBoardService.findScroll(request, user.getId());

        assertAll(
            () -> assertThat(response.getLastId()).isEqualTo(freeBoards.get(freeBoards.size() - 1).getId()),
            () -> assertThat(response.isHasNext()).isEqualTo(hasNext),
            () -> verify(freeBoardRepository).searchScroll(any(), any())
        );
    }

    @DisplayName("게시판 시작 스크롤을 조회한다.")
    @MethodSource("generateScrollTestWithoutLastIdAugments")
    @ParameterizedTest
    void findBoardByScrollWithoutLastId(FreeBoardScrollRequest request, List<FreeBoard> freeBoards, boolean hasNext) {
        when(freeBoardRepository.searchScroll(any(), any())).thenReturn(FreeBoardResponse.listOf(freeBoards));

        FreeBoardScrollResponse response = freeBoardService.findScroll(request, user.getId());

        assertAll(
            () -> assertThat(response.getLastId()).isEqualTo(freeBoards.get(freeBoards.size() - 1).getId()),
            () -> assertThat(response.isHasNext()).isEqualTo(hasNext),
            () -> verify(freeBoardRepository).searchScroll(any(), any())
        );
    }

    @DisplayName("게시판 검색 시작 스크롤을 조회")
    @Test
    void findBoardWithKeywordScrollWithoutLastId() {
        FreeBoard freeBoard1 = FreeBoard.builder().id(95L).content("test").title("게시판").build();
        FreeBoard freeBoard2 = FreeBoard.builder().id(90L).content("게시판 입니다.").title("test").build();
        freeBoard1.associateUser(user);
        freeBoard2.associateUser(user);
        FreeBoardKeyWordRequest request = FreeBoardKeyWordRequest.builder().keyword("test").size(2).build();
        List<FreeBoardResponse> responses = FreeBoardResponse.listOf(List.of(freeBoard1, freeBoard2));
        when(freeBoardRepository.searchScrollWithKeyword(any(), any())).thenReturn(responses);

        FreeBoardScrollResponse response = freeBoardService.findScrollWithKeyword(request, null);

        assertAll(
            () -> assertThat(response.getLastId()).isEqualTo(90L),
            () -> assertThat(response.isHasNext()).isTrue(),
            () -> verify(freeBoardRepository).searchScrollWithKeyword(any(), any())
        );
    }

    @DisplayName("게시판 검색 스크롤 조회")
    @Test
    void findBoardWithKeywordScroll() {
        FreeBoard freeBoard1 = FreeBoard.builder().id(95L).content("test").title("게시판").build();
        FreeBoard freeBoard2 = FreeBoard.builder().id(90L).content("게시판 입니다.").title("test").build();
        freeBoard1.associateUser(user);
        freeBoard2.associateUser(user);
        FreeBoardKeyWordRequest request = FreeBoardKeyWordRequest
            .builder().lastId(100L).keyword("test").size(5).build();

        when(freeBoardRepository.searchScrollWithKeyword(any(), any()))
            .thenReturn(FreeBoardResponse.listOf(List.of(freeBoard1, freeBoard2)));

        FreeBoardScrollResponse response = freeBoardService.findScrollWithKeyword(request, null);

        assertAll(
            () -> assertThat(response.getLastId()).isEqualTo(90L),
            () -> assertThat(response.isHasNext()).isFalse(),
            () -> verify(freeBoardRepository).searchScrollWithKeyword(any(), any())
        );
    }

    @DisplayName("랭킹 게시판 조회")
    @Test
    void findRankFreeBoards() {
        freeBoardService.findRankFreeBoards(user);

        verify(freeBoardRepository).searchRankBoard(any(), any(), any(), any());
    }

    @DisplayName("이미지를 등록한다.")
    @Test
    void uploadImage() throws IOException {
        String filePath = "./test.png";
        when(freeBoardRepository.findById(any())).thenReturn(Optional.of(freeBoard));
        when(freeBoardUploadService.storeFile(any())).thenReturn(filePath);
        MockMultipartFile multipartFile = createMultipartFile();

        freeBoardService.uploadImage(user, freeBoard.getId(), multipartFile);

        assertAll(
            () -> verify(freeBoardRepository).findById(any()),
            () -> verify(freeBoardUploadService).storeFile(any()),
            () -> assertThat(freeBoard.getImgUrl()).isEqualTo(filePath)
        );
    }

    @DisplayName("이미지를 경로를 가져온다.")
    @Test
    void getFreeBoardImageFullPath() {
        String filePath = "test.png";
        when(freeBoardUploadService.getFullPath(any())).thenReturn("./test.png");

        freeBoardService.getFreeBoardImageFullPath(filePath);

        verify(freeBoardUploadService).getFullPath(any());
    }

    private MockMultipartFile createMultipartFile() {
        return new MockMultipartFile("image",
            "test.png",
            "image/png",
            "테스트용 그림".getBytes());
    }

    private static Stream<Arguments> generateScrollTestAugments() {
        return Stream.of(
            Arguments.arguments(FreeBoardScrollRequest.builder().lastId(100L).size(10).build(),
                generateFreeBoards(91L, 10), true),
            Arguments.arguments(FreeBoardScrollRequest.builder().lastId(100L).size(10).build(),
                generateFreeBoards(91L, 5), false)
        );
    }

    private static Stream<Arguments> generateScrollTestWithoutLastIdAugments() {
        return Stream.of(
            Arguments.arguments(FreeBoardScrollRequest.builder().size(10).build(),
                generateFreeBoards(100L, 10), true),
            Arguments.arguments(FreeBoardScrollRequest.builder().size(10).build(),
                generateFreeBoards(100L, 5), false)
        );
    }

    private static List<FreeBoard> generateFreeBoards(Long startId, int size) {
        User owner = User.builder().id(3L).password("1234").build();
        List<FreeBoard> freeBoards = new ArrayList<>();
        for (Long i = startId; i > startId - size; i--) {
            FreeBoard freeBoard = FreeBoard.builder().id(startId + i).title("게시판" + i).content("게시판" + i).build();
            freeBoard.associateUser(owner);
            freeBoards.add(freeBoard);
        }
        return freeBoards;
    }
}
