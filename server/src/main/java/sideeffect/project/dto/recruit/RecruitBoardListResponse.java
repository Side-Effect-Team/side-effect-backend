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
public class RecruitBoardListResponse {

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

    public static RecruitBoardListResponse of(RecruitBoard recruitBoard) {
        return RecruitBoardListResponse.builder()
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

    public static RecruitBoardListResponse ofLike(RecruitBoardAndLikeDto recruitBoardAndLikeDto) {
        return RecruitBoardListResponse.builder()
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

    public static List<RecruitBoardListResponse> listOf(List<RecruitBoard> recruitBoards) {
        return recruitBoards.stream()
                .map(RecruitBoardListResponse::of)
                .collect(Collectors.toList());
    }

    public static List<RecruitBoardListResponse> listOfLike(List<RecruitBoardAndLikeDto> recruitBoards) {
        return recruitBoards.stream()
                .map(RecruitBoardListResponse::ofLike)
                .collect(Collectors.toList());
    }
}
