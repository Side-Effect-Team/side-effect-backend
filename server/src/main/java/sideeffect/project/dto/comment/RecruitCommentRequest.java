package sideeffect.project.dto.comment;

import lombok.*;
import sideeffect.project.domain.comment.RecruitComment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitCommentRequest {

    @NotNull
    private Long boardId;

    @NotBlank
    private String content;

    public RecruitComment toComment() {
        return RecruitComment.builder()
                .content(this.content)
                .build();
    }
}
