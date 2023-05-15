package sideeffect.project.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static sideeffect.project.domain.user.UserRoleType.ROLE_USER;
import static sideeffect.project.dto.freeboard.OrderType.COMMENT;
import static sideeffect.project.dto.freeboard.OrderType.LATEST;
import static sideeffect.project.dto.freeboard.OrderType.LIKE;
import static sideeffect.project.dto.freeboard.OrderType.VIEWS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import sideeffect.project.common.jpa.TestDataRepository;
import sideeffect.project.domain.comment.Comment;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.like.Like;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollDto;
import sideeffect.project.dto.freeboard.OrderType;

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
            .userRoleType(ROLE_USER)
            .build();
        em.persist(user);
        em.flush();
        em.clear();
    }

    @DisplayName("게시판의 목록 중 마지막 게시판을 querydsl 조회")
    @Test
    void searchScroll() {
        int pagingSize = 5;
        generateFreeBoards(10);
        Long lastId = getLastBoardId();
        List<Long> answerBoardIds =
            LongStream.rangeClosed(lastId - pagingSize + 1, lastId).sorted().boxed().collect(Collectors.toList());
        Collections.reverse(answerBoardIds);
        FreeBoardScrollDto scrollDto = FreeBoardScrollDto.builder().orderType(LATEST).size(pagingSize).build();

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
        generateFreeBoards(20);
        Long lastId = getLastBoardId();
        List<Long> answerBoardIds =
            LongStream.rangeClosed(lastId - 19, lastId - 10).sorted().boxed().collect(Collectors.toList());
        Collections.reverse(answerBoardIds);
        FreeBoardScrollDto scrollDto =
            FreeBoardScrollDto.builder().orderType(LATEST).lastId(lastId - 9).size(pagingSize).build();

        List<FreeBoardResponse> freeBoards = repository
            .searchScroll(scrollDto, null);
        List<Long> resultBoardId = freeBoards.stream().map(FreeBoardResponse::getId).collect(Collectors.toList());

        assertAll(
            () -> assertThat(freeBoards).hasSize(10),
            () -> assertThat(resultBoardId).isEqualTo(answerBoardIds)
        );
    }

    @DisplayName("검색 결과를 페이징 방식으로 querydsl 조회")
    @Test
    void searchFreeBoardScrollWithKeyWord() {
        String title = "검색할 제목";
        generateFreeBoards(10);
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
            .keyword(title).orderType(LATEST).lastId(freeBoard2.getId() + 1).size(pagingSize).build();

        List<FreeBoardResponse> boards = repository.searchScrollWithKeyword(scrollDto, null);
        List<Long> boardIds = boards.stream().map(FreeBoardResponse::getId).collect(Collectors.toList());

        assertThat(boardIds).containsExactly(freeBoard2.getId(), freeBoard1.getId());
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

    @DisplayName("댓글 순으로 스크롤을 진행한다.")
    @Test
    void scrollOrderByComment() {
        List<Integer> commentNumbers = List.of(11, 11, 11, 11, 11, 8, 7, 6, 5, 4);
        List<FreeBoard> freeBoards = generateFreeBoards(10);
        associateCommentsAndFreeBoards(freeBoards, commentNumbers);

        List<FreeBoardResponse> responses = scrollFreeBoards(COMMENT);
        List<Integer> result = responses.stream().map(FreeBoardResponse::getCommentNum).collect(Collectors.toList());

        assertThat(commentNumbers).isEqualTo(result);
    }

    @DisplayName("좋아요 순으로 스크롤을 진행한다.")
    @Test
    void scrollOrderByLike() {
        List<Integer> likeNumbers = List.of(23, 22, 11, 11, 11, 8, 8, 6, 5, 4);
        List<FreeBoard> freeBoards = generateFreeBoards(10);
        associateLikesAndFreeBoards(freeBoards, likeNumbers);

        List<FreeBoardResponse> responses = scrollFreeBoards(LIKE);
        List<Integer> result = responses.stream().map(FreeBoardResponse::getLikeNum).collect(Collectors.toList());

        assertThat(likeNumbers).isEqualTo(result);
    }

    @DisplayName("댓글 순으로 스크롤을 진행한다.")
    @Test
    void scrollOrderByView() {
        List<Integer> viewsNumbers = List.of(11, 11, 11, 11, 11, 8, 7, 6, 5, 4);
        List<FreeBoard> freeBoards = generateFreeBoards(10);
        increaseFreeBoardsViews(freeBoards, viewsNumbers);

        List<FreeBoardResponse> responses = scrollFreeBoards(VIEWS);
        List<Long> resultIds = responses.stream().map(FreeBoardResponse::getId).collect(Collectors.toList());
        List<FreeBoard> boards = repository.findAllById(resultIds);

        assertThat(viewsNumbers).isEqualTo(boards.stream().map(FreeBoard::getViews).collect(Collectors.toList()));
    }

    private List<FreeBoardResponse> scrollFreeBoards(OrderType orderType) {
        FreeBoardScrollDto dto1 = FreeBoardScrollDto.builder().size(5).orderType(orderType).build();
        List<FreeBoardResponse> responses1 = repository.searchScroll(dto1, null);
        FreeBoardScrollDto dto2 = FreeBoardScrollDto.builder()
            .lastId(responses1.get(responses1.size() - 1).getId()).size(5).orderType(orderType).build();
        List<FreeBoardResponse> responses2 = repository.searchScroll(dto2, null);
        responses1.addAll(responses2);
        return responses1;
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
        em.flush();
        System.out.println("getLastBoardId() = " + getLastUserId());
    }

    private List<FreeBoard> generateFreeBoards(int size) {
        List<FreeBoard> freeBoards = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            User user = User.builder().userRoleType(ROLE_USER).nickname("게시판 유저" + i).build();
            em.persist(user);
            FreeBoard freeBoard = FreeBoard.builder().title("게시판" + i).content("게시판 내용" + i).build();
            freeBoard.associateUser(user);
            freeBoards.add(freeBoard);
            repository.save(freeBoard);
        }
        em.flush();
        return freeBoards;
    }

    private void increaseFreeBoardsViews(List<FreeBoard> freeBoards, List<Integer> viewsNumbers) {
        for (int i = 0; i < freeBoards.size(); i++) {
            Integer views = viewsNumbers.get(i);
            FreeBoard freeBoard = freeBoards.get(i);
            IntStream.range(0, views).forEach((num) -> freeBoard.increaseViews());
        }
        em.flush();
    }

    private void associateLikesAndFreeBoards(List<FreeBoard> freeBoards, List<Integer> commentNumbers) {
        for (int i = 0; i < freeBoards.size(); i++) {
            FreeBoard freeBoard = freeBoards.get(i);
            Integer likeNum = commentNumbers.get(i);
            like(freeBoard, likeNum);
        }
        em.flush();
    }

    private void associateCommentsAndFreeBoards(List<FreeBoard> freeBoards, List<Integer> commentNumbers) {
        for (int i = 0; i < freeBoards.size(); i++) {
            List<Comment> comments = generateComments(commentNumbers.get(i));
            User writer = generateUser();
            FreeBoard freeBoard = freeBoards.get(i);
            comments.forEach(comment -> {
                comment.associate(writer, freeBoard);
                em.persist(comment);
            });
        }
        em.flush();
    }

    private List<Comment> generateComments(int size) {
        return IntStream.range(0, size).mapToObj((id) -> new Comment("댓글" + id))
            .collect(Collectors.toList());
    }

    private void like(FreeBoard freeBoard, Integer likeNum) {
        for (int i = 0; i < likeNum; i++) {
            User user = User.builder().nickname("추천한 유저" + i).build();
            em.persist(user);
            Like like = Like.like(user, freeBoard);
            em.persist(like);
        }
    }

    private Long getLastBoardId() {
        List<FreeBoard> boards = repository.findAll();
        return boards.get(boards.size() - 1).getId();
    }

    private Long getLastUserId() {
        List<User> users = em.createQuery("select u from User u order by u.id desc", User.class)
            .setMaxResults(1)
            .getResultList();
        Optional<Long> lastId = users.stream().findFirst().map(User::getId);

        return lastId.orElse(0L);
    }

    private User generateUser() {
        User generatedUser = User.builder().build();
        em.persist(generatedUser);
        return generatedUser;
    }
}
