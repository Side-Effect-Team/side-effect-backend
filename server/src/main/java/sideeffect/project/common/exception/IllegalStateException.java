package sideeffect.project.common.exception;

public class IllegalStateException extends BaseException{
    public IllegalStateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
