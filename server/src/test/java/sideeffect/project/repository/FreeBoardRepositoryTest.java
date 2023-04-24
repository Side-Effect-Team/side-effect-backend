package sideeffect.project.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import sideeffect.project.domain.freeboard.FreeBoard;

@DataJpaTest
class FreeBoardRepositoryTest {

    @Autowired
    private FreeBoardRepository repository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        List<FreeBoard> freeBoards = new ArrayList<>();
        for (int i = 0; i < 20 ; i++) {
            FreeBoard freeBoard = FreeBoard.builder().title("게시판" + i).content("내용" + i).build();
            freeBoards.add(freeBoard);
        }
        repository.saveAll(freeBoards);
        em.clear();
    }

    @DisplayName("게시판의 내용을 바탕으로 검색")
    @Test
    void findAllByContentContaining() {
        String content = "검색할 내용";
        FreeBoard freeBoard = FreeBoard.builder().title("게시판").content("----" + content + "abcde").build();
        repository.save(freeBoard);
        repository.save(FreeBoard.builder().title("다른 게시판").content("1234").build());

        List<FreeBoard> boards = repository.findAllByContentContainingOrTitleContaining(content, null);

        assertThat(boards).containsExactly(freeBoard);
    }

    @DisplayName("게시판의 제목을 바탕으로 검색")
    @Test
    void findAllByTitleContaining() {
        String title = "검색할 제목";
        FreeBoard freeBoard = FreeBoard.builder().title("게시판" + title).content("내용").build();
        repository.save(freeBoard);
        repository.save(FreeBoard.builder().title("다른 게시판").content("1234").build());

        List<FreeBoard> boards = repository.findAllByContentContainingOrTitleContaining(null, title);

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
            .findLastPagingBoards(Pageable.ofSize(pagingSize));
        List<Long> resultBoardId = freeBoards.stream().map(FreeBoard::getId).collect(Collectors.toList());

        assertAll(
            () -> assertThat(freeBoards).hasSize(pagingSize),
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

    private Long getLastId() {
        List<FreeBoard> boards = repository.findAll();
        return boards.get(boards.size() - 1).getId();
    }
}
