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
import lombok.ToString;
import sideeffect.project.domain.freeboard.FreeBoard;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FreeBoardResponse {

    private Long id;
    private String imgUrl;
    private String subTitle;
    private Integer views;
    private String title;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private boolean like;
    private int likeNum;
    private int commentNum;
    public static List<FreeBoardResponse> listOf(List<FreeBoard> freeBoards) {
        return freeBoards.stream()
            .map(FreeBoardResponse::of)
            .collect(Collectors.toList());
    }

    public static FreeBoardResponse of(FreeBoard freeBoard) {
        return FreeBoardResponse.builder()
            .id(freeBoard.getId())
            .title(freeBoard.getTitle())
            .views(freeBoard.getViews())
            .subTitle(freeBoard.getSubTitle())
            .imgUrl(freeBoard.getImgUrl())
            .likeNum(freeBoard.getLikes().size())
            .commentNum(freeBoard.getComments().size())
            .createdAt(freeBoard.getCreateAt())
            .build();
    }
}
