package sideeffect.project.domain.recruit;

import com.fasterxml.jackson.annotation.JsonCreator;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.InvalidValueException;

import java.util.stream.Stream;

public enum ProgressType {
    ONLINE("online"), OFFLINE("offline");

    private final String value;

    ProgressType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProgressType parsing(String value) {
        return Stream.of(ProgressType.values())
                .filter(progressType -> progressType.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.RECRUIT_BOARD_PROGRESS_TYPE_NOT_FOUND));
    }
}
