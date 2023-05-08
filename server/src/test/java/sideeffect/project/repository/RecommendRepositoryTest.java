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
import sideeffect.project.domain.recommend.Recommend;
import sideeffect.project.domain.user.User;

class RecommendRepositoryTest extends TestDataRepository {

    @Autowired
    private RecommendRepository recommendRepository;

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
    void existsRecommend() {
        Recommend recommend = Recommend.recommend(user, freeBoard);
        recommendRepository.saveAndFlush(recommend);
        em.clear();

        boolean result = recommendRepository.existsByUserIdAndFreeBoardId(user.getId(), freeBoard.getId());
        assertThat(result).isTrue();
    }

    @DisplayName("유저가 해당 게시판을 추천하지 않았으면 false 반환")
    @Test
    void notExistsRecommend() {
        boolean result = recommendRepository.existsByUserIdAndFreeBoardId(user.getId(), freeBoard.getId());
        assertThat(result).isFalse();
    }

    @DisplayName("게시판과 연관관계가 끊기면 자동으로 삭제된다.")
    @Test
    void deleteRecommend() {
        Recommend recommend = Recommend.recommend(user, freeBoard);
        recommendRepository.saveAndFlush(recommend);
        em.clear();

        deleteRecommend(recommend.getId());
        em.flush();
        em.clear();

        assertThat(recommendRepository.findById(recommend.getId())).isEmpty();
    }

    private void deleteRecommend(Long id) {
        Recommend recommend = recommendRepository.findById(id).orElseThrow(EntityExistsException::new);
        recommend.getFreeBoard().deleteRecommend(recommend);
    }
}
