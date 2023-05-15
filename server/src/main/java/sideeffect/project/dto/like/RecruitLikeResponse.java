package sideeffect.project.dto.like;

import lombok.*;
import sideeffect.project.domain.like.RecruitLike;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitLikeResponse {
    private Long recruitBoardId;
    private String userNickname;
    private String message;

    public static RecruitLikeResponse of(RecruitLike recruitLike, LikeResult message) {
        return RecruitLikeResponse.builder()
                .recruitBoardId(recruitLike.getRecruitBoard().getId())
                .userNickname(recruitLike.getUser().getNickname())
                .message(message.getMessage())
                .build();
    }
}
