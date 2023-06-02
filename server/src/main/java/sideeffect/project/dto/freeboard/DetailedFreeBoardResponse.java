package sideeffect.project.dto.freeboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import java.util.List;
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
    private String imgUrl;
    private String subTitle;
    private int views;
    private Long userId;
    private String writer;
    private String title;
    private String content;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String projectUrl;
    private String projectName;
    private boolean like;
    private int likeNum;
    private List<CommentResponse> comments;

    public static DetailedFreeBoardResponse of(FreeBoard freeBoard, boolean like) {
        DetailedFreeBoardResponse response = DetailedFreeBoardResponse.builder()
            .id(freeBoard.getId())
            .views(freeBoard.getViews())
            .title(freeBoard.getTitle())
            .writer(freeBoard.getUser().getNickname())
            .subTitle(freeBoard.getSubTitle())
            .userId(freeBoard.getUser().getId())
            .content(freeBoard.getContent())
            .projectUrl(freeBoard.getProjectUrl())
            .projectName(freeBoard.getProjectName())
            .imgUrl(freeBoard.getImgUrl())
            .likeNum(freeBoard.getLikes().size())
            .comments(CommentResponse.listOf(freeBoard.getComments()))
            .createdAt(freeBoard.getCreateAt())
            .build();
        response.like = like;
        return response;
    }
}
