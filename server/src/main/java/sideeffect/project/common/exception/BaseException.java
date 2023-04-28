package sideeffect.project.common.exception;

public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }
}
