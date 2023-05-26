package sideeffect.project.dto.comment;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.comment.Comment;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

    private Long commentId;
    private Long boardId;
    private String content;
    private String writer;
    private Long writerId;

    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
            .commentId(comment.getId())
            .writer(comment.getUser().getNickname())
            .boardId(comment.getFreeBoard().getId())
            .content(comment.getContent())
            .writerId(comment.getUser().getId())
            .build();
    }

    public static List<CommentResponse> listOf(List<Comment> comments) {
        return comments.stream().map(CommentResponse::of).collect(Collectors.toList());
    }
}
