package sideeffect.project.domain.position;

import com.fasterxml.jackson.annotation.JsonCreator;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.InvalidValueException;

import java.util.stream.Stream;

public enum PositionType {
    FRONTEND("frontend"), BACKEND("backend"), DESIGNER("designer"), DEVOPS("devops"), MARKETER("marketer"), PM("pm");

    private final String value;

    PositionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PositionType parsing(String value) {
        return Stream.of(PositionType.values())
                .filter(positionType -> positionType.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.POSITION_NOT_FOUND));
    }

}
