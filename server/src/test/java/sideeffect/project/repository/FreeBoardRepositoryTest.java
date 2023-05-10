package sideeffect.project.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import sideeffect.project.common.jpa.TestDataRepository;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.like.Like;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollDto;

class FreeBoardRepositoryTest extends TestDataRepository {

    @Autowired
    private FreeBoardRepository repository;

    @Autowired
    private EntityManager em;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("tester@naver.com")
            .password("1234")
            .nickname("hello")
            .userRoleType(UserRoleType.ROLE_USER)
            .build();
        em.persist(user);
        List<FreeBoard> freeBoards = new ArrayList<>();
        for (int i = 0; i < 20 ; i++) {
            FreeBoard freeBoard = FreeBoard.builder().title("게시판" + i).content("내용" + i).build();
            freeBoards.add(freeBoard);
            freeBoard.associateUser(user);
        }
        repository.saveAll(freeBoards);
        em.flush();
        em.clear();
    }

    @DisplayName("게시판의 내용을 바탕으로 검색")
    @Test
    void findAllByContentContaining() {
        String content = "검색할 내용";
        FreeBoard freeBoard = FreeBoard.builder().title("게시판").content("----" + content + "abcde").build();
        freeBoard.associateUser(user);
        repository.save(freeBoard);
        repository.save(FreeBoard.builder().title("다른 게시판").content("1234").build());

        List<FreeBoard> boards = repository.findStartScrollOfBoardsWithKeyWord(content, Pageable.ofSize(5));

        assertThat(boards).containsExactly(freeBoard);
    }

    @DisplayName("게시판의 제목을 바탕으로 검색")
    @Test
    void findAllByTitleContaining() {
        String title = "검색할 제목";
        FreeBoard freeBoard = FreeBoard.builder().title("게시판" + title).content("내용").build();
        freeBoard.associateUser(user);
        repository.save(freeBoard);
        repository.save(FreeBoard.builder().title("다른 게시판").content("1234").build());

        List<FreeBoard> boards = repository.findStartScrollOfBoardsWithKeyWord(title, Pageable.ofSize(5));

        assertThat(boards).containsExactly(freeBoard);
    }

    @DisplayName("게시판의 목록 중 마지막 게시판을 조회")
    @Test
    void findLastPagingBoards() {
        int pagingSize = 5;
        Long lastId = getLastId();
        List<Long> answerBoardIds =
            LongStream.rangeClosed(lastId - pagingSize + 1, lastId).sorted().boxed().collect(Collectors.toList());
        Collections.reverse(answerBoardIds);

        List<FreeBoard> freeBoards = repository
            .findStartScrollOfBoard(Pageable.ofSize(pagingSize));
        List<Long> resultBoardId = freeBoards.stream().map(FreeBoard::getId).collect(Collectors.toList());

        assertAll(
            () -> assertThat(freeBoards).hasSize(pagingSize),
            () -> assertThat(resultBoardId).isEqualTo(answerBoardIds)
        );
    }

    @DisplayName("게시판의 목록 중 마지막 게시판을 querydsl 조회")
    @Test
    void searchScroll() {
        int pagingSize = 5;
        Long lastId = getLastId();
        List<Long> answerBoardIds =
            LongStream.rangeClosed(lastId - pagingSize + 1, lastId).sorted().boxed().collect(Collectors.toList());
        Collections.reverse(answerBoardIds);
        FreeBoardScrollDto scrollDto = FreeBoardScrollDto.builder().size(pagingSize).build();

        List<FreeBoardResponse> freeBoards = repository
            .searchScroll(scrollDto, null);
        List<Long> resultBoardId = freeBoards.stream().map(FreeBoardResponse::getId).collect(Collectors.toList());

        assertAll(
            () -> assertThat(freeBoards).hasSize(pagingSize),
            () -> assertThat(resultBoardId).isEqualTo(answerBoardIds)
        );
    }

    @DisplayName("스크롤 페이징 방식으로 querydsl 조회")
    @Test
    void searchScrollByBoardId() {
        int pagingSize = 10;
        Long lastId = getLastId();
        List<Long> answerBoardIds =
            LongStream.rangeClosed(lastId - 19, lastId - 10).sorted().boxed().collect(Collectors.toList());
        Collections.reverse(answerBoardIds);
        FreeBoardScrollDto scrollDto = FreeBoardScrollDto.builder().lastId(lastId - 9).size(pagingSize).build();

        List<FreeBoardResponse> freeBoards = repository
            .searchScroll(scrollDto, null);
        List<Long> resultBoardId = freeBoards.stream().map(FreeBoardResponse::getId).collect(Collectors.toList());

        assertAll(
            () -> assertThat(freeBoards).hasSize(10),
            () -> assertThat(resultBoardId).isEqualTo(answerBoardIds)
        );
    }


    @DisplayName("스크롤 페이징 방식으로 조회")
    @Test
    void findByPaging() {
        Long lastId = getLastId();
        List<Long> answerBoardIds =
            LongStream.rangeClosed(lastId - 19, lastId - 10).sorted().boxed().collect(Collectors.toList());
        Collections.reverse(answerBoardIds);

        List<FreeBoard> freeBoards = repository
            .findByIdLessThanOrderByIdDesc(lastId - 9, Pageable.ofSize(10));
        List<Long> resultBoardId = freeBoards.stream().map(FreeBoard::getId).collect(Collectors.toList());

        assertAll(
            () -> assertThat(freeBoards).hasSize(10),
            () -> assertThat(resultBoardId).isEqualTo(answerBoardIds)
        );
    }

    @DisplayName("검색 결과를 페이징 방식으로 querydsl 조회")
    @Test
    void searchFreeBoardScrollWithKeyWord() {
        String title = "검색할 제목";
        int pagingSize =5;
        FreeBoard freeBoard1 = FreeBoard.builder().title("게시판" + title).content("내용").build();
        FreeBoard freeBoard2 = FreeBoard.builder().title("게시판" + title).content("내용").build();
        FreeBoard freeBoard3 = FreeBoard.builder().title("게시판" + title).content("내용").build();
        freeBoard1.associateUser(user);
        freeBoard2.associateUser(user);
        freeBoard3.associateUser(user);
        repository.save(freeBoard1);
        repository.save(freeBoard2);
        repository.save(freeBoard3);
        FreeBoardScrollDto scrollDto = FreeBoardScrollDto.builder()
            .keyword(title).lastId(freeBoard2.getId() + 1).size(pagingSize).build();

        List<FreeBoardResponse> boards = repository
            .searchScrollWithKeyword(scrollDto, null);
        List<Long> boardIds = boards.stream().map(FreeBoardResponse::getId).collect(Collectors.toList());

        assertThat(boardIds).containsExactly(freeBoard2.getId(), freeBoard1.getId());
    }

    @DisplayName("검색 결과를 페이징 방식으로 조회")
    @Test
    void findFreeBoardScrollWithKeyWord() {
        String title = "검색할 제목";
        FreeBoard freeBoard1 = FreeBoard.builder().title("게시판" + title).content("내용").build();
        FreeBoard freeBoard2 = FreeBoard.builder().title("게시판" + title).content("내용").build();
        FreeBoard freeBoard3 = FreeBoard.builder().title("게시판" + title).content("내용").build();
        repository.save(freeBoard1);
        repository.save(freeBoard2);
        repository.save(freeBoard3);

        List<FreeBoard> boards = repository
            .findScrollOfBoardsWithKeyWord(title, freeBoard2.getId() + 1, Pageable.ofSize(5));

        assertThat(boards).containsExactly(freeBoard2, freeBoard1);
    }

    @DisplayName("추천수가 많은 순으로 게시판이 조회된다.")
    @Test
    void findRankFreeBoard() {
        int rankSize = 6;
        int boardsSize = 10;
        List<Integer> recommendNumbers = List.of(11, 9, 11, 23, 21, 8, 6, 7, 2, 0);
        saveFreeBoardsAndLike(boardsSize, recommendNumbers);
        em.flush();
        em.clear();

        List<Integer> result = repository.findRankFreeBoard(Pageable.ofSize(rankSize))
            .stream().map(FreeBoard::getLikes).map(Set::size).collect(Collectors.toList());
        System.out.println("result = " + result);
        System.out.println(repository.findRankFreeBoard(Pageable.ofSize(rankSize)));
        assertThat(result).isEqualTo(List.of(23, 21, 11, 11, 9, 8));
    }

    private void saveFreeBoardsAndLike(int boardSize, List<Integer> recommendNumbers) {
        for (int i = 0; i < boardSize; i++) {
            User user = User.builder().nickname("유저" + i).build();
            em.persist(user);
            FreeBoard freeBoard = FreeBoard.builder().title("게시판" + i).build();
            freeBoard.associateUser(user);
            repository.save(freeBoard);
            like(freeBoard, recommendNumbers.get(i));
        }
    }

    private void like(FreeBoard freeBoard, Integer recommendNumber) {
        for (int i = 0; i < recommendNumber; i++) {
            User user = User.builder().nickname("추천한 유저" + i).build();
            em.persist(user);
            Like like = Like.like(user, freeBoard);
            em.persist(like);
        }
    }

    private Long getLastId() {
        List<FreeBoard> boards = repository.findAll();
        return boards.get(boards.size() - 1).getId();
    }
}
