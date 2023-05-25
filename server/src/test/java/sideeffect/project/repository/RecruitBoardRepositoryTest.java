package sideeffect.project.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sideeffect.project.common.jpa.TestDataRepository;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.applicant.ApplicantStatus;
import sideeffect.project.domain.like.RecruitLike;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.BoardStack;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.applicant.ApplicantListResponse;
import sideeffect.project.dto.recruit.RecruitBoardAndLikeDto;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RecruitBoardRepositoryTest extends TestDataRepository {

    @Autowired
    RecruitBoardRepository recruitBoardRepository;

    @Autowired
    ApplicantRepository applicantRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    StackRepository stackRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    private Position frontEndPosition;
    private Position backEndPosition;
    private Stack javascriptStack;
    private Stack javaStack;
    private User user;

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

        user = User.builder()
                .nickname("tester")
                .password("1234")
                .userRoleType(UserRoleType.ROLE_USER)
                .email("test@naver.com")
                .build();

        userRepository.save(user);

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

        List<RecruitBoardAndLikeDto> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(user.getId(),lastId - 19, "", null, 10);
        List<Long> returnBoardIds = findRecruitBoards.stream().map(RecruitBoardAndLikeDto::getRecruitBoard).map(RecruitBoard::getId).collect(Collectors.toList());

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

        List<RecruitBoardAndLikeDto> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(user.getId(),null, searchTitle, null, 5);
        List<RecruitBoard> findRecruitBoardsOfList = findRecruitBoards.stream().map(RecruitBoardAndLikeDto::getRecruitBoard).collect(Collectors.toList());

        assertAll(
                () -> assertThat(findRecruitBoardsOfList).containsExactly(recruitBoardInSearchTitle),
                () -> assertThat(findRecruitBoardsOfList).doesNotContain(recruitBoardNotInSearchTitle)
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

        List<RecruitBoardAndLikeDto> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(user.getId(), null, searchContents, null, 5);
        List<RecruitBoard> findRecruitBoardsOfList = findRecruitBoards.stream().map(RecruitBoardAndLikeDto::getRecruitBoard).collect(Collectors.toList());

        assertAll(
                () -> assertThat(findRecruitBoardsOfList).containsExactly(recruitBoardInSearchContents),
                () -> assertThat(findRecruitBoardsOfList).doesNotContain(recruitBoardNotInSearchContents)
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

        List<RecruitBoardAndLikeDto> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(user.getId(), recruitBoard2.getId(), searchContents, null, 5);
        List<RecruitBoard> findRecruitBoardsOfList = findRecruitBoards.stream().map(RecruitBoardAndLikeDto::getRecruitBoard).collect(Collectors.toList());

        assertAll(
                () -> assertThat(findRecruitBoardsOfList).containsExactly(recruitBoard1),
                () -> assertThat(findRecruitBoardsOfList).doesNotContain(recruitBoard2),
                () -> assertThat(findRecruitBoardsOfList).hasSize(1)
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

        List<RecruitBoardAndLikeDto> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(user.getId(), null, "", searchStacks, 5);
        List<RecruitBoard> findRecruitBoardsOfList = findRecruitBoards.stream().map(RecruitBoardAndLikeDto::getRecruitBoard).collect(Collectors.toList());

        assertAll(
                () -> assertThat(findRecruitBoardsOfList).containsExactly(recruitBoardInJavaScriptStack, recruitBoardInJavaStack),
                () -> assertThat(findRecruitBoardsOfList).doesNotContain(recruitBoardNotInStack)
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

        List<RecruitBoardAndLikeDto> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(user.getId(), recruitBoardInJavaScriptStack.getId(), "", searchStacks, 5);
        List<RecruitBoard> findRecruitBoardsOfList = findRecruitBoards.stream().map(RecruitBoardAndLikeDto::getRecruitBoard).collect(Collectors.toList());

        assertAll(
                () -> assertThat(findRecruitBoardsOfList).containsExactly(recruitBoardInJavaStack),
                () -> assertThat(findRecruitBoardsOfList).doesNotContain(recruitBoardInJavaScriptStack),
                () -> assertThat(findRecruitBoardsOfList).hasSize(1)
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

        List<RecruitBoardAndLikeDto> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(user.getId(), null, searchKeyword, searchStacks, 5);
        List<RecruitBoard> findRecruitBoardsOfList = findRecruitBoards.stream().map(RecruitBoardAndLikeDto::getRecruitBoard).collect(Collectors.toList());

        assertAll(
                () -> assertThat(findRecruitBoardsOfList).containsExactly(boardInKeywordWithStacks),
                () -> assertThat(findRecruitBoardsOfList).doesNotContain(boardInKeywordWithOtherStacks),
                () -> assertThat(findRecruitBoardsOfList).doesNotContain(boardInKeyword),
                () -> assertThat(findRecruitBoardsOfList).doesNotContain(boardInStacks),
                () -> assertThat(findRecruitBoardsOfList).doesNotContain(boardNotInKeywordWithStacks)
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

        List<RecruitBoardAndLikeDto> findRecruitBoards = recruitBoardRepository.findWithSearchConditions(user.getId(), boardInKeywordWithStacks2.getId(), searchKeyword, searchStacks, 5);
        List<RecruitBoard> findRecruitBoardsOfList = findRecruitBoards.stream().map(RecruitBoardAndLikeDto::getRecruitBoard).collect(Collectors.toList());

        assertAll(
                () -> assertThat(findRecruitBoardsOfList).containsExactly(boardInKeywordWithStacks1),
                () -> assertThat(findRecruitBoardsOfList).doesNotContain(boardInKeywordWithStacks2),
                () -> assertThat(findRecruitBoardsOfList).hasSize(1)
        );
    }

    @DisplayName("게시판에 사용자의 중복지원을 확인한다.")
    @Test
    void existsApplicantByRecruitBoard() {
        RecruitBoard recruitBoard = RecruitBoard.builder().title("모집 게시판").contents("내용").build();
        BoardPosition boardPosition = BoardPosition.builder().position(backEndPosition).targetNumber(3).build();
        recruitBoard.addBoardPosition(boardPosition);
        RecruitBoard savedBoard = recruitBoardRepository.save(recruitBoard);

        boolean first = recruitBoardRepository.existsApplicantByRecruitBoard(savedBoard.getId(), user.getId());

        Applicant applicant = Applicant.builder().build();
        applicant.associate(user, boardPosition);
        applicantRepository.save(applicant);

        boolean second = recruitBoardRepository.existsApplicantByRecruitBoard(savedBoard.getId(), user.getId());

        assertAll(
                () -> assertThat(first).isFalse(),
                () -> assertThat(second).isTrue()
        );
    }

    @DisplayName("게시판에 지원한 사용자의 목록을 조회한다.")
    @Test
    void getApplicantsByPosition() {
        RecruitBoard recruitBoard = RecruitBoard.builder().title("모집 게시판").contents("내용").build();
        BoardPosition boardPositionBack = BoardPosition.builder().position(backEndPosition).targetNumber(3).build();
        BoardPosition boardPositionFront = BoardPosition.builder().position(frontEndPosition).targetNumber(3).build();
        recruitBoard.addBoardPosition(boardPositionBack);
        recruitBoard.addBoardPosition(boardPositionFront);
        RecruitBoard savedBoard = recruitBoardRepository.save(recruitBoard);

        Applicant applicant1 = Applicant.builder().build();
        applicant1.associate(user, boardPositionBack);
        applicantRepository.save(applicant1);

        Applicant applicant2 = Applicant.builder().build();
        applicant2.associate(user, boardPositionFront);
        applicantRepository.save(applicant2);

        List<ApplicantListResponse> applicantListResponses = recruitBoardRepository.getApplicantsByPosition(savedBoard.getId(), ApplicantStatus.PENDING);

        assertAll(
                () -> assertThat(applicantListResponses).hasSize(2)
        );
    }

    @DisplayName("게시판에 모집된 지원자의 목록을 조회한다.")
    @Test
    void getApplicantsByPositionApproved() {
        RecruitBoard recruitBoard = RecruitBoard.builder().title("모집 게시판").contents("내용").build();
        BoardPosition boardPositionBack = BoardPosition.builder().position(backEndPosition).targetNumber(3).build();
        BoardPosition boardPositionFront = BoardPosition.builder().position(frontEndPosition).targetNumber(3).build();
        recruitBoard.addBoardPosition(boardPositionBack);
        recruitBoard.addBoardPosition(boardPositionFront);
        RecruitBoard savedBoard = recruitBoardRepository.save(recruitBoard);

        Applicant applicant1 = Applicant.builder().build();
        applicant1.associate(user, boardPositionBack);
        applicantRepository.save(applicant1);

        Applicant applicant2 = Applicant.builder().build();
        applicant2.associate(user, boardPositionFront);
        applicantRepository.save(applicant2);

        applicant1.updateStatus(ApplicantStatus.APPROVED);
        applicant2.updateStatus(ApplicantStatus.APPROVED);

        List<ApplicantListResponse> applicantListResponses = recruitBoardRepository.getApplicantsByPosition(savedBoard.getId(), ApplicantStatus.APPROVED);

        assertAll(
                () -> assertThat(applicantListResponses).hasSize(2)
        );
    }

    @DisplayName("게시글 상세 조회할 때 좋아요 여부 확인")
    @Test
    void detailRecruitBoardWithLike() {
        RecruitBoard recruitBoard = RecruitBoard.builder().title("모집 게시판").contents("내용").build();
        RecruitBoard savedRecruitboard = recruitBoardRepository.save(recruitBoard);

        RecruitLike recruitLike = RecruitLike.createRecruitLike(user, recruitBoard);
        em.persist(recruitLike);

        RecruitBoardAndLikeDto likeDto = recruitBoardRepository.findByBoardIdAndUserId(savedRecruitboard.getId(), user.getId()).orElse(null);

        assertAll(
                () -> assertThat(likeDto.isLike()).isTrue(),
                () -> assertThat(likeDto.getRecruitBoard().getRecruitLikes()).hasSize(1)
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

    private void like(RecruitBoard recruitBoard, Integer likeNum) {
        for (int i = 0; i < likeNum; i++) {
            User user = User.builder().nickname("추천한 유저" + i).build();
            em.persist(user);
            RecruitLike recruitLike = RecruitLike.createRecruitLike(user, recruitBoard);
            em.persist(recruitLike);
        }
    }

}
