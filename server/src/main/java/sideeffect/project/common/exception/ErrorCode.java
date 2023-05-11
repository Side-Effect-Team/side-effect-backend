package sideeffect.project.common.exception;

public enum ErrorCode {

    USER_NOT_FOUND(400, "U_001", "유저를 찾을 수 없습니다."),
    USER_UNAUTHORIZED(403, "U_002", "해당 유저가 아닙니다."),
    USER_ALREADY_EXIST(400, "U_003", "이미 존재하는 이메일입니다"),
    USER_NOT_NULL(400, "U_004", "유저 ID에 Null값이 들어올 수 없습니다"),

    TOKEN_EXPIRED(401, "T_001", "토큰이 만료되었습니다"),

    FREE_BOARD_NOT_FOUND(400, "FB_001", "해당 게시판을 찾을 수 없습니다."),
    FREE_BOARD_UNAUTHORIZED(403, "FB_002", "해당 게시판에 대한 권한이 없습니다."),
    FREE_BOARD_DUPLICATE(400, "FB_003", "projectUrl이 중복되었습니다."),

    COMMENT_NOT_FOUND(400, "CM_001", "해당 댓글을 찾을 수 없습니다."),
    COMMENT_UNAUTHORIZED(403, "CM_002", "해당 댓글에 대한 권한이 없습니다."),

    RECRUIT_BOARD_NOT_FOUND(400, "RB_001", "해당 모집 게시판을 찾을 수 없습니다."),
    RECRUIT_BOARD_TYPE_NOT_FOUND(400, "RB_002", "모집 게시판의 유형을 찾을 수 없습니다."),
    RECRUIT_BOARD_PROGRESS_TYPE_NOT_FOUND(400, "RB_003", "모집 게시판의 진행방식을 찾을 수 없습니다."),
    RECRUIT_BOARD_UNAUTHORIZED(403, "RB_004", "해당 모집 게시판에 대한 권한이 없습니다."),

    BOARD_POSITION_NOT_FOUND(400, "BP_001", "해당 게시판이 모집하는 포지션을 찾을 수 없습니다."),
    BOARD_POSITION_FULL(409, "BP_002", "해당 포지션은 전부 모집되었습니다."),

    APPLICANT_NOT_FOUND(400, "AC_001", "지원자를 찾을 수 없습니다."),
    APPLICANT_UNAUTHORIZED(403, "AC_002", "해당 지원자 목록에 대한 권한이 없습니다."),
    APPLICANT_SELF_UNAUTHORIZED(403, "AC_003", "본인의 게시물에는 지원할 수 없습니다."),
    APPLICANT_DUPLICATED(403, "AC_004", "하나의 게시물에는 한 번만 지원할 수 있습니다."),
    APPLICANT_EXISTS(409, "AC_005", "해당 지원자는 이미 팀원으로 합류가 되어있습니다."),
    APPLICANT_NOT_EXISTS(409, "AC_006", "해당 팀원은 존재하지 않습니다."),

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
