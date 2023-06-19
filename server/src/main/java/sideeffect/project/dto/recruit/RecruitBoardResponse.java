package sideeffect.project.dto.recruit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sideeffect.project.domain.recruit.RecruitBoard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonFormat.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardResponse {

    private Long id;
    private boolean closed;
    private String title;
    private int views;
    private boolean like;
    private int likeNum;
    private int commentNum;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private List<String> positions;
    private List<String> tags;
    @JsonIgnore
    private List<BoardPositionResponse> positionsList;

    public static RecruitBoardResponse of(RecruitBoard recruitBoard) {
        return RecruitBoardResponse.builder()
                .id(recruitBoard.getId())
                .title(recruitBoard.getTitle())
                .views(recruitBoard.getViews())
                .likeNum(recruitBoard.getRecruitLikes().size())
                .commentNum(recruitBoard.getRecruitComments().size())
                .createdAt(recruitBoard.getCreateAt())
                .positions(getPositionList(recruitBoard))
                .tags(getStackList(recruitBoard))
                .positionsList(BoardPositionResponse.listOf(recruitBoard.getBoardPositions()))
                .build();
    }

    private static List<String> getStackList(RecruitBoard recruitBoard) {
        List<String> stackList = recruitBoard.getBoardStacks().stream()
                .map(boardStack -> boardStack.getStack().getStackType().getValue())
                .collect(Collectors.toList());
        return stackList;
    }

    private static List<String> getPositionList(RecruitBoard recruitBoard) {
        List<String> positionList = recruitBoard.getBoardPositions().stream()
                .map(boardPosition -> boardPosition.getPosition().getPositionType().getValue())
                .collect(Collectors.toList());
        return positionList;
    }

    public static RecruitBoardResponse ofLike(RecruitBoardAndLikeDto recruitBoardAndLikeDto) {
        return RecruitBoardResponse.builder()
                .id(recruitBoardAndLikeDto.getRecruitBoard().getId())
                .title(recruitBoardAndLikeDto.getRecruitBoard().getTitle())
                .views(recruitBoardAndLikeDto.getRecruitBoard().getViews())
                .like(recruitBoardAndLikeDto.isLike())
                .likeNum(recruitBoardAndLikeDto.getRecruitBoard().getRecruitLikes().size())
                .commentNum(recruitBoardAndLikeDto.getRecruitBoard().getRecruitComments().size())
                .createdAt(recruitBoardAndLikeDto.getRecruitBoard().getCreateAt())
                .positions(getPositionList(recruitBoardAndLikeDto.getRecruitBoard()))
                .tags(getStackList(recruitBoardAndLikeDto.getRecruitBoard()))
                .positionsList(BoardPositionResponse.listOf(recruitBoardAndLikeDto.getRecruitBoard().getBoardPositions()))
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

    public void updateClosed() {
        this.closed = true;
    }

}
