package sideeffect.project.dto.comment;

import javax.validation.constraints.NotBlank;
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
public class CommentRequest {

    private Long freeBoardId;

    @NotBlank
    private String comment;

    public Comment toComment() {
        return new Comment(comment);
    }
}
