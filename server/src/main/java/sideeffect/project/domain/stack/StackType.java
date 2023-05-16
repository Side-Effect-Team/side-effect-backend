package sideeffect.project.domain.stack;

import com.fasterxml.jackson.annotation.JsonCreator;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.InvalidValueException;

import java.util.stream.Stream;

public enum StackType {
    JAVASCRIPT("javascript"), TYPESCRIPT("typescript"), REACT("react"), VUE("vue"),
    SVELTE("svelte"), NEXT_JS("nextjs"), NEST_JS("nestjs"), NODE_JS("nodejs"),
    JAVA("java"), SPRING("spring"), GO("go");

    private final String value;

    StackType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static StackType parsing(String value) {
        return Stream.of(StackType.values())
                .filter(stackType -> stackType.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.STACK_NOT_FOUND));
    }

    public static StackType of(String value) {
        return Stream.of(StackType.values())
                .filter(stackType -> stackType.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

}
