package sideeffect.project.common.exception;

public enum ErrorCode {

    USER_MOT_FOUND(400, "U_001", "유저를 찾을 수 없습니다."),

    FREE_BOARD_NOT_FOUND(400, "FB_001", "해당 게시판을 찾을 수 없습니다."),

    COMMENT_NOT_FOUND(400, "CM_001", "해당 댓글을 찾을 수 없습니다."),
    COMMENT_UNAUTHORIZED(403, "CM_002", "해당 댓글에 대한 권한이 없습니다.");

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
