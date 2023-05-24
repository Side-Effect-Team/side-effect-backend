package sideeffect.project.dto.recruit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import sideeffect.project.domain.recruit.RecruitBoard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardResponse {

    private Long id;
    private Long userId;
    private String title;
    private String projectName;
    private String content;
    private String imgSrc;
    private int views;
    private boolean like;
    private int likeNum;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private List<BoardPositionResponse> positions;
    private List<BoardStackResponse> tags;

    public static RecruitBoardResponse of(RecruitBoard recruitBoard) {
        return RecruitBoardResponse.builder()
                .id(recruitBoard.getId())
                .userId(recruitBoard.getUser().getId())
                .projectName(recruitBoard.getProjectName())
                .title(recruitBoard.getTitle())
                .content(recruitBoard.getContents())
                .imgSrc(recruitBoard.getImgSrc())
                .views(recruitBoard.getViews())
                .likeNum(recruitBoard.getRecruitLikes().size())
                .createdAt(recruitBoard.getCreateAt())
                .positions(BoardPositionResponse.listOf(recruitBoard.getBoardPositions()))
                .tags(BoardStackResponse.listOf(recruitBoard.getBoardStacks()))
                .build();
    }

    public static RecruitBoardResponse ofLike(RecruitBoardAndLikeDto recruitBoardAndLikeDto) {
        return RecruitBoardResponse.builder()
                .id(recruitBoardAndLikeDto.getRecruitBoard().getId())
                .userId(recruitBoardAndLikeDto.getRecruitBoard().getUser().getId())
                .projectName(recruitBoardAndLikeDto.getRecruitBoard().getProjectName())
                .title(recruitBoardAndLikeDto.getRecruitBoard().getTitle())
                .content(recruitBoardAndLikeDto.getRecruitBoard().getContents())
                .imgSrc(recruitBoardAndLikeDto.getRecruitBoard().getImgSrc())
                .views(recruitBoardAndLikeDto.getRecruitBoard().getViews())
                .like(recruitBoardAndLikeDto.isLike())
                .likeNum(recruitBoardAndLikeDto.getRecruitBoard().getRecruitLikes().size())
                .createdAt(recruitBoardAndLikeDto.getRecruitBoard().getCreateAt())
                .positions(BoardPositionResponse.listOf(recruitBoardAndLikeDto.getRecruitBoard().getBoardPositions()))
                .tags(BoardStackResponse.listOf(recruitBoardAndLikeDto.getRecruitBoard().getBoardStacks()))
                .build();
    }

    public static List<RecruitBoardResponse> listOf(List<RecruitBoard> recruitBoards) {
        return recruitBoards.stream()
                .map(RecruitBoardResponse::of)
                .collect(Collectors.toList());
    }

    public static List<RecruitBoardResponse> listOfLike(List<RecruitBoardAndLikeDto> recruitBoards) {
        return recruitBoards.stream()
                .map(RecruitBoardResponse::ofLike)
                .collect(Collectors.toList());
    }

}
