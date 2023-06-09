package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.like.Like;
import sideeffect.project.domain.like.RecruitLike;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.BoardStack;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LikeBoardResponse {

    private String category;
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Boolean like;
    private int likeNum;
    private int views;
    private int commentNum;
    private String imgUrl;
    private List<StackType> tags;
    private List<PositionType> positions;
    private Boolean closed;

    public static List<LikeBoardResponse> listOf(User user){
        List<LikeBoardResponse> likeBoardResponses = new ArrayList<>();
        Set<Like> likes = user.getLikes();
        List<RecruitLike> recruitLikes = user.getRecruitLikes();

        if(likes!=null && !likes.isEmpty()){
            likeBoardResponses.addAll(likes.stream()
                    .map(like -> getLikeBoardOfFree(like))
                    .collect(Collectors.toList()));
        }

        if(recruitLikes!=null && !recruitLikes.isEmpty()){
            likeBoardResponses.addAll(recruitLikes.stream()
                    .map(recruitLike -> getLikeBoardOfRecruit(recruitLike))
                    .collect(Collectors.toList()));
        }
        return likeBoardResponses;
    }

    private static LikeBoardResponse getLikeBoardOfRecruit(RecruitLike recruitLike) {
        RecruitBoard recruitBoard = recruitLike.getRecruitBoard();
        return LikeBoardResponse.builder()
                .category("recruits")
                .id(recruitBoard.getId())
                .title(recruitBoard.getTitle())
                .createdAt(recruitBoard.getCreateAt())
                .like(true)
                .likeNum(recruitBoard.getRecruitLikes().size())
                .views(recruitBoard.getViews())
                .tags(getStackType(recruitBoard.getBoardStacks()))
                .positions(getPositionType(recruitBoard.getBoardPositions()))
                .closed(isClosed(recruitBoard))
                .build();
    }

    private static Boolean isClosed(RecruitBoard recruitBoard) {
        Boolean flag = true;
        for (BoardPosition boardPosition : recruitBoard.getBoardPositions()) {
            if(boardPosition.getCurrentNumber()!=boardPosition.getTargetNumber()){
                flag = false;
                break;
            }
        }
        return flag;
    }

    private static List<PositionType> getPositionType(List<BoardPosition> boardPositions) {
        List<PositionType> positionTypes = Collections.emptyList();
        if(boardPositions!=null && !boardPositions.isEmpty()){
            positionTypes = boardPositions.stream()
                    .map(boardPosition -> boardPosition.getPosition().getPositionType())
                    .collect(Collectors.toList());
        }
        return positionTypes;
    }

    private static List<StackType> getStackType(List<BoardStack> boardStacks) {
        List<StackType> stackTypes = Collections.emptyList();
        if(boardStacks!=null && !boardStacks.isEmpty()){
            stackTypes = boardStacks.stream()
                    .map(boardStack -> boardStack.getStack().getStackType())
                    .collect(Collectors.toList());
        }
        return stackTypes;
    }

    private static LikeBoardResponse getLikeBoardOfFree(Like like) {
        FreeBoard freeBoard = like.getFreeBoard();
        return LikeBoardResponse.builder()
                .category("projects")
                .id(freeBoard.getId())
                .title(freeBoard.getTitle())
                .content(freeBoard.getContent())
                .createdAt(freeBoard.getCreateAt())
                .commentNum(freeBoard.getComments().size())
                .like(true)
                .likeNum(freeBoard.getLikes().size())
                .views(freeBoard.getViews())
                .imgUrl(freeBoard.getImgUrl())
                .build();
    }


}
