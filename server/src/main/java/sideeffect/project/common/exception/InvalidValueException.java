package sideeffect.project.common.exception;

public class InvalidValueException extends BaseException {

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
