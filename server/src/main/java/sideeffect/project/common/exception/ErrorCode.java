package sideeffect.project.common.exception;

public enum ErrorCode {

    USER_NOT_FOUND(400, "U_001", "유저를 찾을 수 없습니다."),
    USER_UNAUTHORIZED(403, "U_002", "해당 유저가 아닙니다."),
    USER_ALREADY_EXIST(400, "U_003", "이미 존재하는 이메일입니다"),
    USER_NOT_NULL(400, "U_004", "유저 ID에 Null값이 들어올 수 없습니다"),
    USER_UNAUTHENTICATION(400, "U_005", "유효하지 않은 이메일이거나 비밀번호가 틀립니다"),
    USER_FILE_UPLOAD_FAILED(500, "U_006", "이미지 업로드에 문제가 발생했습니다."),
    USER_SOCIAL_ACCESS_TOKEN_EXPIRED(401, "U_007", "소셜 서버의 액세스 토큰이 만료되었습니다"),

    ACCESS_TOKEN_ERROR(401, "AT_006", "비정상적인 액세스 토큰입니다"),
    ACCESS_TOKEN_UNSUPPORTED(401, "AT_002", "지원하지 않는 액세스 토큰 형식입니다"),
    ACCESS_TOKEN_MALFORMED(401, "AT_003", "잘못된 액세스 토큰 구조입니다"),
    ACCESS_TOKEN_SIGNATURE_FAILED(401, "AT_004", "유효하지 않은 서명입니다"),
    ACCESS_TOKEN_EXPIRED(401, "AT_001", "엑세스 토큰이 만료되었습니다"),
    ACCESS_TOKEN_ILLEGAL_STATE(401, "AT_005", "엑세스 토큰이 비어있습니다"),
    REFRESH_TOKEN_NOT_FOUND(401, "RT_001", "유효하지 않은 리프레쉬 토큰입니다"),
    REFRESH_TOKEN_NOT_REQUEST(401, "RT_002", "토큰이 전달되지 않았습니다."),

    NOTIFICATION_NOT_FOUND(400, "N_001", "해당 알림을 찾을 수 없습니다."),

    FREE_BOARD_NOT_FOUND(400, "FB_001", "해당 게시판을 찾을 수 없습니다."),
    FREE_BOARD_UNAUTHORIZED(403, "FB_002", "해당 게시판에 대한 권한이 없습니다."),
    FREE_BOARD_DUPLICATE(400, "FB_003", "projectUrl이 중복되었습니다."),
    FREE_BOARD_FILE_UPLOAD_FAILED(500, "FB_003", "이미지 업로드에 문제가 발생했습니다."),

    COMMENT_NOT_FOUND(400, "CM_001", "해당 댓글을 찾을 수 없습니다."),
    COMMENT_UNAUTHORIZED(403, "CM_002", "해당 댓글에 대한 권한이 없습니다."),

    RECRUIT_BOARD_NOT_FOUND(400, "RB_001", "해당 모집 게시판을 찾을 수 없습니다."),
    RECRUIT_BOARD_UNAUTHORIZED(403, "RB_002", "해당 모집 게시판에 대한 권한이 없습니다."),
    RECRUIT_BOARD_FILE_UPLOAD_FAILED(500, "RB_003", "이미지 업로드에 문제가 발생했습니다."),
    RECRUIT_BOARD_FILE_DOWNLOAD_FAILED(500, "RB_004", "이미지 다운로드에 문제가 발생했습니다."),

    RECRUIT_COMMENT_NOT_FOUND(400, "RC_001", "해당 댓글을 찾을 수 없습니다."),
    RECRUIT_COMMENT_UNAUTHORIZED(403, "RC_002", "해당 댓글에 대한 권한이 없습니다."),

    BOARD_POSITION_NOT_FOUND(400, "BP_001", "해당 게시판이 모집하는 포지션을 찾을 수 없습니다."),
    BOARD_POSITION_FULL(409, "BP_002", "해당 포지션은 전부 모집되었습니다."),
    BOARD_POSITION_ALREADY_EXISTS(400, "BP_003", "해당 포지션은 이미 모집중인 포지션이므로, 추가할 수 없습니다."),

    APPLICANT_NOT_FOUND(400, "AC_001", "지원자를 찾을 수 없습니다."),
    APPLICANT_UNAUTHORIZED(403, "AC_002", "해당 지원자 목록에 대한 권한이 없습니다."),
    APPLICANT_SELF_UNAUTHORIZED(403, "AC_003", "본인의 게시물에는 지원할 수 없습니다."),
    APPLICANT_DUPLICATED(403, "AC_004", "하나의 게시물에는 한 번만 지원할 수 있습니다."),
    APPLICANT_EXISTS(409, "AC_005", "해당 지원자는 이미 팀원으로 합류가 되어있습니다."),
    APPLICANT_NOT_EXISTS(409, "AC_006", "해당 팀원은 존재하지 않습니다."),
    APPLICANT_UNAUTHORIZED_CANCEL(403, "AC_007", "지원자 본인만 지원 취소할 수 있습니다."),
    APPLICANT_REJECTED_CANCEL(409, "AC_008", "해당 지원자는 이미 거절이 되어있습니다."),
    APPLICANT_PENALTY(403, "AC_009", "패널티가 부과되어 지원할 수 없습니다."),

    POSITION_NOT_FOUND(400, "PS_001", "해당 포지션을 찾을 수 없습니다."),

    STACK_NOT_FOUND(400, "ST_001", "해당 기술스택을 찾을 수 없습니다."),

    INVALID_FILTER_VALUE(400, "F_001", "게시판 조회 필터를 잘못입력했습니다."),

    FILE_NOT_FOUND(400, "FI_001", "파일을 찾을 수 없습니다.");

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
