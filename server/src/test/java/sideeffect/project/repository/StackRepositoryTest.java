package sideeffect.project.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sideeffect.project.common.jpa.TestDataRepository;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

class StackRepositoryTest extends TestDataRepository {

    @Autowired
    private StackRepository repository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        Stack stack = Stack.builder().stackType(StackType.SPRING).build();
        repository.save(stack);
        em.clear();
    }

    @DisplayName("enum타입으로 기술스택을 조회한다.")
    @Test
    void findByStackType() {
        Stack findStack = repository.findByStackType(StackType.SPRING).orElse(null);

        Assertions.assertAll(
                () -> assertThat(findStack).isNotNull(),
                () -> assertThat(findStack.getStackType()).isEqualTo(StackType.SPRING)
        );
    }

}
