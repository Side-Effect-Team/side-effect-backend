package sideeffect.project.dto.comment;

import lombok.*;
import sideeffect.project.domain.comment.RecruitComment;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitCommentResponse {

    private Long commentId;
    private Long recruitBoardId;
    private String content;
    private String writer;
    private Long writerId;

    public static RecruitCommentResponse of(RecruitComment recruitComment) {
        return RecruitCommentResponse.builder()
            .commentId(recruitComment.getId())
            .writer(recruitComment.getUser().getNickname())
            .recruitBoardId(recruitComment.getRecruitBoard().getId())
            .content(recruitComment.getContent())
            .writerId(recruitComment.getUser().getId())
            .build();
    }

    public static List<RecruitCommentResponse> listOf(List<RecruitComment> recruitComments) {
        return recruitComments.stream().map(RecruitCommentResponse::of).collect(Collectors.toList());
    }
}
