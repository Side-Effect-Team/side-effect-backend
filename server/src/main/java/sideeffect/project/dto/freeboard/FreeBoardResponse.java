package sideeffect.project.dto.freeboard;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.freeboard.FreeBoard;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FreeBoardResponse {

    private Long id;
    private int views;
    private Long userId;
    private String title;
    private String content;
    private String projectUrl;
    private String imgUrl;

    public static List<FreeBoardResponse> listOf(List<FreeBoard> freeBoards) {
        return freeBoards.stream()
            .map(FreeBoardResponse::of)
            .collect(Collectors.toList());
    }

    public static FreeBoardResponse of(FreeBoard freeBoard) {
        return FreeBoardResponse.builder()
            .id(freeBoard.getId())
            .views(freeBoard.getViews())
            .title(freeBoard.getTitle())
            .userId(freeBoard.getUser().getId())
            .content(freeBoard.getContent())
            .projectUrl(freeBoard.getProjectUrl())
            .imgUrl(freeBoard.getImgUrl())
            .build();
    }
}
