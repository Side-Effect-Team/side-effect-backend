package sideeffect.project.dto.recommend;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.recommend.Recommend;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendResponse {
    private Long boardId;
    private String userNickname;
    private String message;

    public static RecommendResponse of(Recommend recommend, RecommendResult message) {
        return RecommendResponse.builder()
            .boardId(recommend.getFreeBoard().getId())
            .userNickname(recommend.getUser().getNickname())
            .message(message.getMessage())
            .build();
    }
}
