package sideeffect.project.domain.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sideeffect.project.common.jpa.TestDataRepository;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.repository.FreeBoardRepository;
import sideeffect.project.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

class BaseTimeEntityTest extends TestDataRepository {

    @Autowired
    private FreeBoardRepository freeBoardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .password("1234")
            .build();
        userRepository.save(user);
    }

    @DisplayName("엔티티가 저장되면 createAt이 설정")
    @Test
    void create() {
        FreeBoard freeBoard = saveFreeBoard();

        assertThat(freeBoard.getCreateAt()).isNotNull();
    }

    @DisplayName("엔티티가 업데이트되면 updateAt이 설정")
    @Test
    void update() {
        FreeBoard freeBoard = saveFreeBoard();
        FreeBoard updateBoard = FreeBoard.builder().content("update").build();
        LocalDateTime updateAt = freeBoard.getUpdateAt();

        freeBoard.update(updateBoard);
        em.flush();

        assertAll(
            () -> assertThat(freeBoard.getUpdateAt()).isNotNull(),
            () -> assertThat(freeBoard.getUpdateAt()).isNotEqualTo(updateAt)
        );
        System.out.println("freeBoard = " + freeBoard.getCreateAt());
        System.out.println("freeBoard = " + freeBoard.getUpdateAt());
    }

    private FreeBoard saveFreeBoard() {
        FreeBoard freeBoard = FreeBoard.builder().title("hello").content("world").build();
        freeBoard.associateUser(user);
        freeBoardRepository.save(freeBoard);
        return freeBoard;
    }
}
