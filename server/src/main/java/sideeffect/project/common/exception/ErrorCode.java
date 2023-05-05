package sideeffect.project.common.exception;

public enum ErrorCode {

    USER_NOT_FOUND(400, "U_001", "유저를 찾을 수 없습니다."),

    FREE_BOARD_NOT_FOUND(400, "FB_001", "해당 게시판을 찾을 수 없습니다."),

    COMMENT_NOT_FOUND(400, "CM_001", "해당 댓글을 찾을 수 없습니다."),
    COMMENT_UNAUTHORIZED(403, "CM_002", "해당 댓글에 대한 권한이 없습니다."),

    RECRUIT_BOARD_NOT_FOUND(400, "RB_001", "해당 모집 게시판을 찾을 수 없습니다."),
    RECRUIT_BOARD_UNAUTHORIZED(403, "RB_002", "해당 모집 게시판에 대한 권한이 없습니다."),

    BOARD_POSITION_NOT_FOUND(400, "BP_001", "해당 게시판이 모집하는 포지션을 찾을 수 없습니다."),

    APPLICANT_NOT_FOUND(400, "AC_001", "지원자를 찾을 수 없습니다."),
    APPLICANT_SELF_UNAUTHORIZED(403, "AC_002", "본인의 게시물에는 지원할 수 없습니다."),
    APPLICANT_DUPLICATED(403, "AC_003", "하나의 게시물에는 한 번만 지원할 수 있습니다."),

    POSITION_NOT_FOUND(400, "PS_001", "해당 포지션을 찾을 수 없습니다."),

    STACK_NOT_FOUND(400, "ST_001", "해당 기술스택을 찾을 수 없습니다.");

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
