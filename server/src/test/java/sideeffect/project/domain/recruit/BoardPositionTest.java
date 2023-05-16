package sideeffect.project.domain.recruit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoardPositionTest {

    @DisplayName("포지션의 모집 인원 수를 증가시킨다.")
    @Test
    void increaseCurrentNumber() {
        BoardPosition boardPosition = BoardPosition.builder().id(1L).targetNumber(1).build();

        int before = boardPosition.getCurrentNumber();

        boardPosition.increaseCurrentNumber();

        assertThat(boardPosition.getCurrentNumber()).isEqualTo(before + 1);
    }

    @DisplayName("포지션의 모집 인원 수를 감소시킨다.")
    @Test
    void decreaseCurrentNumber() {
        BoardPosition boardPosition = BoardPosition.builder().id(1L).targetNumber(1).build();
        boardPosition.increaseCurrentNumber();

        int before = boardPosition.getCurrentNumber();
        boardPosition.decreaseCurrentNumber();

        assertThat(boardPosition.getCurrentNumber()).isEqualTo(before - 1);
    }

}