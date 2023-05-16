package sideeffect.project.convertor;

import static org.assertj.core.api.Assertions.assertThat;
import static sideeffect.project.dto.freeboard.OrderType.COMMENT;
import static sideeffect.project.dto.freeboard.OrderType.LATEST;
import static sideeffect.project.dto.freeboard.OrderType.LIKE;
import static sideeffect.project.dto.freeboard.OrderType.VIEWS;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sideeffect.project.common.converter.OrderTypeRequestConverter;
import sideeffect.project.dto.freeboard.OrderType;

class OrderTypeConverterTest {

    private OrderTypeRequestConverter orderTypeRequestConverter;

    @BeforeEach
    void setUp() {
        orderTypeRequestConverter = new OrderTypeRequestConverter();
    }

    @DisplayName("string to orderType test")
    @MethodSource("generateValueAndOrderType")
    @ParameterizedTest
    void convert(String value, OrderType orderType) {
        OrderType result = orderTypeRequestConverter.convert(value);
        assertThat(result).isEqualTo(orderType);
    }

    @DisplayName("빈 문자열이나 null을 받으면 LATEST가 반환")
    @Test
    void convertBlankEndNull() {
        OrderType result1 = orderTypeRequestConverter.convert(null);
        OrderType result2 = orderTypeRequestConverter.convert("");
        assertThat(result1).isEqualTo(LATEST);
        assertThat(result2).isEqualTo(LATEST);
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
