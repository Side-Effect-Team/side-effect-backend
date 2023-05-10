package sideeffect.project.dto.like;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.like.Like;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeResponse {
    private Long boardId;
    private String userNickname;
    private String message;

    public static LikeResponse of(Like like, LikeResult message) {
        return LikeResponse.builder()
            .boardId(like.getFreeBoard().getId())
            .userNickname(like.getUser().getNickname())
            .message(message.getMessage())
            .build();
    }
}
