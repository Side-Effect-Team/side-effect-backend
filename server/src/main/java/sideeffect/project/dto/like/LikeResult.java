package sideeffect.project.dto.like;

public enum LikeResult {

    LIKE("게시글을 추천했습니다."),
    CANCEL_LIKE("게시글을 추천을 취소했습니다.");

    private final String message;

    LikeResult(java.lang.String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
