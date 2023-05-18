package sideeffect.project.dto.freeboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.dto.comment.CommentResponse;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DetailedFreeBoardResponse {

    private Long id;
    private int views;
    private Long userId;
    private String title;
    private String content;
    private String projectUrl;
    private String imgUrl;
    private String projectName;
    private int likeNum;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private List<CommentResponse> comments;

    public static List<DetailedFreeBoardResponse> listOf(List<FreeBoard> freeBoards) {
        return freeBoards.stream()
            .map(DetailedFreeBoardResponse::of)
            .collect(Collectors.toList());
    }

    public static DetailedFreeBoardResponse of(FreeBoard freeBoard) {
        return DetailedFreeBoardResponse.builder()
            .id(freeBoard.getId())
            .views(freeBoard.getViews())
            .title(freeBoard.getTitle())
            .userId(freeBoard.getUser().getId())
            .content(freeBoard.getContent())
            .projectUrl(freeBoard.getProjectUrl())
            .projectName(freeBoard.getProjectName())
            .imgUrl(freeBoard.getImgUrl())
            .likeNum(freeBoard.getLikes().size())
            .comments(CommentResponse.listOf(freeBoard.getComments()))
            .createdAt(freeBoard.getCreateAt())
            .build();
    }
}
