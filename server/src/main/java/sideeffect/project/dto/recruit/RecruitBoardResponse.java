package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.RecruitBoard;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardResponse {

    private Long id;
    private String title;
    private String projectName;
    private String content;
    private String imgSrc;
    private int views;
    private int likeNum;
    private List<BoardPositionResponse> positions;
    private List<BoardStackResponse> tags;

    public static RecruitBoardResponse of(RecruitBoard recruitBoard) {
        return RecruitBoardResponse.builder()
                .id(recruitBoard.getId())
                .projectName(recruitBoard.getProjectName())
                .title(recruitBoard.getTitle())
                .content(recruitBoard.getContents())
                .imgSrc(recruitBoard.getImgSrc())
                .views(recruitBoard.getViews())
                .likeNum(recruitBoard.getRecruitLikes().size())
                .positions(BoardPositionResponse.listOf(recruitBoard.getBoardPositions()))
                .tags(BoardStackResponse.listOf(recruitBoard.getBoardStacks()))
                .build();
    }

    public static List<RecruitBoardResponse> listOf(List<RecruitBoard> recruitBoards) {
        return recruitBoards.stream()
                .map(RecruitBoardResponse::of)
                .collect(Collectors.toList());
    }

}
