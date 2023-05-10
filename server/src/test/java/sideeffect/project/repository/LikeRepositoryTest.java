package sideeffect.project.repository;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sideeffect.project.common.jpa.TestDataRepository;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.like.Like;
import sideeffect.project.domain.user.User;

class LikeRepositoryTest extends TestDataRepository {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private EntityManager em;

    private User user;
    private FreeBoard freeBoard;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("test@naver.com")
            .password("1234")
            .nickname("tester")
            .build();
        em.persist(user);

        freeBoard = FreeBoard.builder()
            .title("제목")
            .content("content")
            .build();
        freeBoard.associateUser(user);
        em.persist(freeBoard);
        em.flush();
        em.clear();
    }

    @DisplayName("유저가 해당 게시판이 추천했으면 true 반환")
    @Test
    void existsLike() {
        Like like = Like.like(user, freeBoard);
        likeRepository.saveAndFlush(like);
        em.clear();

        boolean result = likeRepository.existsByUserIdAndFreeBoardId(user.getId(), freeBoard.getId());
        assertThat(result).isTrue();
    }

    @DisplayName("유저가 해당 게시판을 추천하지 않았으면 false 반환")
    @Test
    void notExistsLike() {
        boolean result = likeRepository.existsByUserIdAndFreeBoardId(user.getId(), freeBoard.getId());
        assertThat(result).isFalse();
    }

    @DisplayName("게시판과 연관관계가 끊기면 자동으로 삭제된다.")
    @Test
    void deleteLike() {
        Like like = Like.like(user, freeBoard);
        likeRepository.saveAndFlush(like);
        em.clear();

        deleteLike(like.getId());
        em.flush();
        em.clear();

        assertThat(likeRepository.findById(like.getId())).isEmpty();
    }

    private void deleteLike(Long id) {
        Like like = likeRepository.findById(id).orElseThrow(EntityExistsException::new);
        like.getFreeBoard().deleteLike(like);
    }
}
