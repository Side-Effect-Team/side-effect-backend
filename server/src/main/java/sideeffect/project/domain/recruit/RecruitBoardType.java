package sideeffect.project.domain.recruit;

import com.fasterxml.jackson.annotation.JsonCreator;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.InvalidValueException;

import java.util.stream.Stream;

public enum RecruitBoardType {
    STUDY("study"), PROJECT("project");

    private final String value;

    RecruitBoardType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RecruitBoardType parsing(String value) {
        return Stream.of(RecruitBoardType.values())
                .filter(recruitBoardType -> recruitBoardType.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.RECRUIT_BOARD_TYPE_NOT_FOUND));
    }
}
