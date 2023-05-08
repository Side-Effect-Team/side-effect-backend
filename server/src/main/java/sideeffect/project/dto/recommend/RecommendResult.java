package sideeffect.project.dto.recommend;

public enum RecommendResult {

    RECOMMEND("게시글을 추천했습니다."),
    CANCEL_RECOMMEND("게시글을 추천을 취소했습니다.");

    private final String message;

    RecommendResult(java.lang.String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
