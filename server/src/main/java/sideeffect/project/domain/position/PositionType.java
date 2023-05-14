package sideeffect.project.domain.position;

import com.fasterxml.jackson.annotation.JsonCreator;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.InvalidValueException;

import java.util.stream.Stream;

public enum PositionType {
    FRONTEND("frontend", "프론트엔드"), BACKEND("backend", "백엔드"), DESIGNER("designer", "디자이너"), DEVOPS("devops", "데브옵스"), MARKETER("marketer", "마케터"), PM("pm", "프로젝트 매니저");

    private final String value;
    private final String koreanName;

    PositionType(String value, String koreanName) {
        this.value = value;
        this.koreanName = koreanName;
    }

    public String getValue() {
        return value;
    }

    public String getKoreanName() {
        return koreanName;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PositionType parsing(String value) {
        return Stream.of(PositionType.values())
                .filter(positionType -> positionType.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.POSITION_NOT_FOUND));
    }

}
