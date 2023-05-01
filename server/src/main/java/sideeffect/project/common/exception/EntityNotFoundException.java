package sideeffect.project.common.exception;

public class EntityNotFoundException extends BaseException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
