package sideeffect.project.dto.freeboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sideeffect.project.dto.freeboard.OrderType.COMMENT;
import static sideeffect.project.dto.freeboard.OrderType.LATEST;
import static sideeffect.project.dto.freeboard.OrderType.LIKE;
import static sideeffect.project.dto.freeboard.OrderType.VIEWS;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sideeffect.project.common.exception.InvalidValueException;

class OrderTypeTest {


    @DisplayName("문자열을 입력하면 value에 맞는 OrderType이 반환된다.")
    @MethodSource("generateValueAndOrderType")
    @ParameterizedTest
    void parse(String value, OrderType orderType) {
        OrderType result = OrderType.parse(value);
        assertThat(result).isEqualTo(orderType);
    }

    @DisplayName("문자열을 잘못 입력하면 예외가 발생한다")
    @Test
    void parseInvalidValue() {
        String invalidValue = "collection";
        assertThatThrownBy(() -> OrderType.parse(invalidValue))
            .isInstanceOf(InvalidValueException.class);
    }

    private static Stream<Arguments> generateValueAndOrderType() {
        return Stream.of(
            Arguments.arguments("latest", LATEST),
            Arguments.arguments("views", VIEWS),
            Arguments.arguments("like", LIKE),
            Arguments.arguments("comment", COMMENT)
        );
    }
}
