package sideeffect.project.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorObject {

    private String fieldName;

    private String message;

    public ErrorObject(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

}
