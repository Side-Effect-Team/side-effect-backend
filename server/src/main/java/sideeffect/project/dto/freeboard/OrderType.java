package sideeffect.project.dto.freeboard;

import static sideeffect.project.common.exception.ErrorCode.INVALID_FILTER_VALUE;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import sideeffect.project.common.exception.InvalidValueException;

public enum OrderType {
    LATEST("latest"),
    VIEWS("views"),
    LIKE("like"),
    COMMENT("comment");

    private final String value;

    OrderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OrderType parse(String value) {
        return Arrays.stream(OrderType.values())
            .filter(orderType -> orderType.getValue().equals(value))
            .findFirst()
            .orElseThrow(() -> new InvalidValueException(INVALID_FILTER_VALUE));
    }
}
