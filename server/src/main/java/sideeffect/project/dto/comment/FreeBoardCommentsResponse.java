package sideeffect.project.dto.comment;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FreeBoardCommentsResponse {
    private List<CommentResponse> commentResponses;

    public static FreeBoardCommentsResponse of(List<CommentResponse> responses) {
        return FreeBoardCommentsResponse.builder()
            .commentResponses(responses)
            .build();
    }
}
