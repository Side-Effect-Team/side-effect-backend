package sideeffect.project.dto.recruit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import sideeffect.project.dto.comment.RecruitCommentResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DetailedRecruitBoardResponse {

    private Long id;
    private Long userId;
    private String writer;
    private String title;
    private String projectName;
    private String content;
    private String imgSrc;
    private int views;
    private boolean like;
    private int likeNum;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private List<DetailedBoardPositionResponse> positions;
    private List<BoardStackResponse> tags;
    private List<RecruitCommentResponse> comments;

    public static DetailedRecruitBoardResponse ofLike(RecruitBoardAndLikeDto recruitBoardAndLikeDto) {
        return DetailedRecruitBoardResponse.builder()
                .id(recruitBoardAndLikeDto.getRecruitBoard().getId())
                .userId(recruitBoardAndLikeDto.getRecruitBoard().getUser().getId())
                .writer(recruitBoardAndLikeDto.getRecruitBoard().getUser().getNickname())
                .projectName(recruitBoardAndLikeDto.getRecruitBoard().getProjectName())
                .title(recruitBoardAndLikeDto.getRecruitBoard().getTitle())
                .content(recruitBoardAndLikeDto.getRecruitBoard().getContents())
                .imgSrc(recruitBoardAndLikeDto.getRecruitBoard().getImgSrc())
                .views(recruitBoardAndLikeDto.getRecruitBoard().getViews())
                .like(recruitBoardAndLikeDto.isLike())
                .likeNum(recruitBoardAndLikeDto.getRecruitBoard().getRecruitLikes().size())
                .createdAt(recruitBoardAndLikeDto.getRecruitBoard().getCreateAt())
                .positions(DetailedBoardPositionResponse.listOf(recruitBoardAndLikeDto.getRecruitBoard().getBoardPositions()))
                .tags(BoardStackResponse.listOf(recruitBoardAndLikeDto.getRecruitBoard().getBoardStacks()))
                .comments(RecruitCommentResponse.listOf(recruitBoardAndLikeDto.getRecruitBoard().getRecruitComments()))
                .build();
    }
}
