package sideeffect.project.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sideeffect.project.common.jpa.TestDataRepository;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

class PositionRepositoryTest extends TestDataRepository {

    @Autowired
    private PositionRepository repository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        Position position = Position.builder().positionType(PositionType.BACKEND).build();
        repository.save(position);
        em.clear();
    }

    @DisplayName("enum타입으로 포지션을 조회한다.")
    @Test
    void findByPositionType() {
        Position findPosition = repository.findByPositionType(PositionType.BACKEND).orElse(null);

        Assertions.assertAll(
                () -> assertThat(findPosition).isNotNull(),
                () -> assertThat(findPosition.getPositionType()).isEqualTo(PositionType.BACKEND)
        );
    }

}
