package sideeffect.project.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.BoardStack;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class RecruitBoardRepositoryTest {

    @Autowired
    RecruitBoardRepository recruitBoardRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    StackRepository stackRepository;

    @Autowired
    EntityManager em;

    private Position frontEndPosition;
    private Position backEndPosition;
    private Stack javascriptStack;
    private Stack javaStack;

    @BeforeEach
    void setUp() {
        frontEndPosition = Position.builder().positionType(PositionType.FRONTEND).build();
        backEndPosition = Position.builder().positionType(PositionType.BACKEND).build();
        positionRepository.save(frontEndPosition);
        positionRepository.save(backEndPosition);
        javascriptStack = Stack.builder().stackType(StackType.JAVASCRIPT).build();
        javaStack = Stack.builder().stackType(StackType.JAVA).build();
        stackRepository.save(javascriptStack);
        stackRepository.save(javaStack);

        em.clear();
    }

    @DisplayName("게시판 스크롤 페이징 조회")
    @Test
    void findRecruitBoardPaging() {
        List<RecruitBoard> recruitBoards = generateRecruitBoards(1L, 40);
        recruitBoardRepository.saveAll(recruitBoards);
        Long lastId = getLastId();
        List<Long> answerBoardIds = LongStream.rangeClosed(lastId - 29, lastId - 20).sorted().boxed().collect(Collectors.toList());
        Collections.reverse(answerBoardIds);

        List<RecruitBoard> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(lastId - 19, "", null, Pageable.ofSize(10));
        List<Long> returnBoardIds = findRecruitBoards.stream().map(RecruitBoard::getId).collect(Collectors.toList());

        assertAll(
                () -> assertThat(findRecruitBoards).hasSize(10),
                () -> assertThat(returnBoardIds).isEqualTo(answerBoardIds)
        );
    }

    @DisplayName("게시판 키워드로 제목 검색")
    @Test
    void findRecruitBoardByTitle() {
        String searchTitle = "검색할 제목";
        RecruitBoard recruitBoardInSearchTitle = RecruitBoard.builder().title("모집 게시판" + searchTitle).contents("내용").build();
        RecruitBoard recruitBoardNotInSearchTitle = RecruitBoard.builder().title("모집 게시판").contents("내용").build();
        recruitBoardRepository.save(recruitBoardInSearchTitle);
        recruitBoardRepository.save(recruitBoardNotInSearchTitle);

        List<RecruitBoard> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(null, searchTitle, null, Pageable.ofSize(5));

        assertAll(
                () -> assertThat(findRecruitBoards).containsExactly(recruitBoardInSearchTitle),
                () -> assertThat(findRecruitBoards).doesNotContain(recruitBoardNotInSearchTitle)
        );
    }

    @DisplayName("게시판 키워드로 내용 검색")
    @Test
    void findRecruitBoardByContents() {
        String searchContents = "검색할 컨텐츠";
        RecruitBoard recruitBoardInSearchContents = RecruitBoard.builder().title("모집 게시판").contents("!@#$%" + searchContents).build();
        RecruitBoard recruitBoardNotInSearchContents = RecruitBoard.builder().title("모집 게시판").contents("!@#$%").build();
        recruitBoardRepository.save(recruitBoardInSearchContents);
        recruitBoardRepository.save(recruitBoardNotInSearchContents);

        List<RecruitBoard> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(null, searchContents, null, Pageable.ofSize(5));

        assertAll(
                () -> assertThat(findRecruitBoards).containsExactly(recruitBoardInSearchContents),
                () -> assertThat(findRecruitBoards).doesNotContain(recruitBoardNotInSearchContents)
        );
    }

    @DisplayName("게시판 키워드와 lastId로 검색")
    @Test
    void findRecruitBoardByKeywordWithLastId() {
        String searchContents = "검색할 키워드";
        RecruitBoard recruitBoard1 = RecruitBoard.builder().title("모집 게시판").contents("!@#$%" + searchContents).build();
        RecruitBoard recruitBoard2 = RecruitBoard.builder().title("모집 게시판" + searchContents).contents("!@#$%").build();
        recruitBoardRepository.save(recruitBoard1);
        recruitBoardRepository.save(recruitBoard2);

        List<RecruitBoard> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(recruitBoard2.getId(), searchContents, null, Pageable.ofSize(5));

        assertAll(
                () -> assertThat(findRecruitBoards).containsExactly(recruitBoard1),
                () -> assertThat(findRecruitBoards).doesNotContain(recruitBoard2),
                () -> assertThat(findRecruitBoards).hasSize(1)
        );
    }

    @DisplayName("게시판 기술스택으로 검색")
    @Test
    void findRecruitBoardByStacks() {
        BoardStack boardJavaStack = BoardStack.builder().stack(javaStack).build();
        RecruitBoard recruitBoardInJavaStack = RecruitBoard.builder().title("모집 게시판").contents("내용").build();
        recruitBoardInJavaStack.addBoardStack(boardJavaStack);

        BoardStack boardJavaInScriptStack = BoardStack.builder().stack(javascriptStack).build();
        RecruitBoard recruitBoardInJavaScriptStack = RecruitBoard.builder().title("모집 게시판").contents("내용").build();
        recruitBoardInJavaScriptStack.addBoardStack(boardJavaInScriptStack);

        RecruitBoard recruitBoardNotInStack = RecruitBoard.builder().title("모집 게시판").contents("내용").build();

        recruitBoardRepository.save(recruitBoardInJavaStack);
        recruitBoardRepository.save(recruitBoardInJavaScriptStack);
        recruitBoardRepository.save(recruitBoardNotInStack);

        List<StackType> searchStacks = new ArrayList<>();
        searchStacks.add(StackType.JAVA);
        searchStacks.add(StackType.JAVASCRIPT);

        List<RecruitBoard> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(null, "", searchStacks, Pageable.ofSize(5));

        assertAll(
                () -> assertThat(findRecruitBoards).containsExactly(recruitBoardInJavaScriptStack, recruitBoardInJavaStack),
                () -> assertThat(findRecruitBoards).doesNotContain(recruitBoardNotInStack)
        );
    }

    @DisplayName("게시판 기술스택과 lastId로 검색")
    @Test
    void findRecruitBoardByStacksWithLastId() {
        BoardStack boardJavaStack = BoardStack.builder().stack(javaStack).build();
        RecruitBoard recruitBoardInJavaStack = RecruitBoard.builder().title("모집 게시판").contents("내용").build();
        recruitBoardInJavaStack.addBoardStack(boardJavaStack);

        BoardStack boardJavaInScriptStack = BoardStack.builder().stack(javascriptStack).build();
        RecruitBoard recruitBoardInJavaScriptStack = RecruitBoard.builder().title("모집 게시판").contents("내용").build();
        recruitBoardInJavaScriptStack.addBoardStack(boardJavaInScriptStack);

        recruitBoardRepository.save(recruitBoardInJavaStack);
        recruitBoardRepository.save(recruitBoardInJavaScriptStack);

        List<StackType> searchStacks = new ArrayList<>();
        searchStacks.add(StackType.JAVA);
        searchStacks.add(StackType.JAVASCRIPT);

        List<RecruitBoard> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(recruitBoardInJavaScriptStack.getId(), "", searchStacks, Pageable.ofSize(5));

        assertAll(
                () -> assertThat(findRecruitBoards).containsExactly(recruitBoardInJavaStack),
                () -> assertThat(findRecruitBoards).doesNotContain(recruitBoardInJavaScriptStack),
                () -> assertThat(findRecruitBoards).hasSize(1)
        );
    }

    @DisplayName("게시판 키워드와 기술스택으로 검색")
    @Test
    void findRecruitBoardByKeywordWithStacks() {
        String searchKeyword = "검색할 키워드";
        BoardStack boardStackJava1 = BoardStack.builder().stack(javaStack).build();
        RecruitBoard boardInKeywordWithStacks = RecruitBoard.builder().title("모집 게시판" + searchKeyword).contents("내용").build();
        boardInKeywordWithStacks.addBoardStack(boardStackJava1);

        BoardStack boardStackJavaScript = BoardStack.builder().stack(javascriptStack).build();
        RecruitBoard boardInKeywordWithOtherStacks = RecruitBoard.builder().title("모집 게시판" + searchKeyword).contents("내용").build();
        boardInKeywordWithStacks.addBoardStack(boardStackJavaScript);

        RecruitBoard boardInKeyword = RecruitBoard.builder().title("모집 게시판" + searchKeyword).contents("내용").build();

        BoardStack boardStackJava2 = BoardStack.builder().stack(javaStack).build();
        RecruitBoard boardInStacks = RecruitBoard.builder().title("모집 게시판").contents("내용").build();
        boardInStacks.addBoardStack(boardStackJava2);

        RecruitBoard boardNotInKeywordWithStacks = RecruitBoard.builder().title("모집 게시판").contents("내용").build();

        recruitBoardRepository.save(boardInKeywordWithStacks);
        recruitBoardRepository.save(boardInKeywordWithOtherStacks);
        recruitBoardRepository.save(boardInKeyword);
        recruitBoardRepository.save(boardInStacks);
        recruitBoardRepository.save(boardNotInKeywordWithStacks);

        List<StackType> searchStacks = new ArrayList<>();
        searchStacks.add(StackType.JAVA);

        List<RecruitBoard> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(null, searchKeyword, searchStacks, Pageable.ofSize(5));

        assertAll(
                () -> assertThat(findRecruitBoards).containsExactly(boardInKeywordWithStacks),
                () -> assertThat(findRecruitBoards).doesNotContain(boardInKeywordWithOtherStacks),
                () -> assertThat(findRecruitBoards).doesNotContain(boardInKeyword),
                () -> assertThat(findRecruitBoards).doesNotContain(boardInStacks),
                () -> assertThat(findRecruitBoards).doesNotContain(boardNotInKeywordWithStacks)
        );
    }

    @DisplayName("게시판 키워드와 기술스택과 lastId로 검색")
    @Test
    void findRecruitBoardByKeywordWithStacksAndLastId() {
        String searchKeyword = "검색할 키워드";
        BoardStack boardStackJava1 = BoardStack.builder().stack(javaStack).build();
        RecruitBoard boardInKeywordWithStacks1 = RecruitBoard.builder().title("모집 게시판" + searchKeyword).contents("내용").build();
        boardInKeywordWithStacks1.addBoardStack(boardStackJava1);

        BoardStack boardStackJava2 = BoardStack.builder().stack(javaStack).build();
        RecruitBoard boardInKeywordWithStacks2 = RecruitBoard.builder().title("모집 게시판" + searchKeyword).contents("내용").build();
        boardInKeywordWithStacks2.addBoardStack(boardStackJava2);


        recruitBoardRepository.save(boardInKeywordWithStacks1);
        recruitBoardRepository.save(boardInKeywordWithStacks2);


        List<StackType> searchStacks = new ArrayList<>();
        searchStacks.add(StackType.JAVA);

        List<RecruitBoard> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(boardInKeywordWithStacks2.getId(), searchKeyword, searchStacks, Pageable.ofSize(5));

        assertAll(
                () -> assertThat(findRecruitBoards).containsExactly(boardInKeywordWithStacks1),
                () -> assertThat(findRecruitBoards).doesNotContain(boardInKeywordWithStacks2),
                () -> assertThat(findRecruitBoards).hasSize(1)
        );
    }

    private Long getLastId() {
        List<RecruitBoard> recruitBoards = recruitBoardRepository.findAll();
        return recruitBoards.get(recruitBoards.size() - 1).getId();
    }

    private static List<RecruitBoard> generateRecruitBoards(Long startId, int size) {
        List<RecruitBoard> recruitBoards = new ArrayList<>();
        for (Long i = startId; i < startId + size; i++) {
            RecruitBoard recruitBoard = RecruitBoard.builder().title("모집 게시판" + i).contents("모집합니다." + i).build();
            recruitBoards.add(recruitBoard);
        }
        return recruitBoards;
    }

}