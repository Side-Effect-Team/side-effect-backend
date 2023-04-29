package sideeffect.project.common.exception;

public enum ErrorCode {

    TERMINOLOGY_NOT_FOUND(400, "FB_001", "해당 게시판을 찾을 수 없습니다.");

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}
