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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class UploadBoardResponse {

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

    public static List<UploadBoardResponse> listOf(User user) {
        List<UploadBoardResponse> uploadBoardResponseList = new ArrayList<>();
        List<FreeBoard> freeBoards = user.getFreeBoards();
        List<RecruitBoard> recruitBoards = user.getRecruitBoards();

        if(freeBoards!=null && !freeBoards.isEmpty()){
            uploadBoardResponseList.addAll(
                    freeBoards.stream()
                            .map(freeBoard -> getUploadBoardOfFree(user, freeBoard))
                            .collect(Collectors.toList())
            );
        }

        if(recruitBoards!=null && !recruitBoards.isEmpty()){
            uploadBoardResponseList.addAll(
                    recruitBoards.stream()
                            .map(recruitBoard -> getUploadBoardOfRecruit(user, recruitBoard))
                            .collect(Collectors.toList())
            );
        }

        return uploadBoardResponseList;
    }

    private static UploadBoardResponse getUploadBoardOfRecruit(User user, RecruitBoard recruitBoard) {
        return UploadBoardResponse.builder()
                .category("recruits")
                .id(recruitBoard.getId())
                .title(recruitBoard.getTitle())
                .createdAt(recruitBoard.getCreateAt())
                .like(isRecruitBoardLiked(user.getId(), recruitBoard.getRecruitLikes()))
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

    private static Boolean isRecruitBoardLiked(Long id, List<RecruitLike> recruitLikes) {
        if(recruitLikes!=null && !recruitLikes.isEmpty()){
            for (RecruitLike recruitLike : recruitLikes) {
                if(recruitLike.getUser().getId()==id) return true;
            }
        }
        return false;
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

    private static UploadBoardResponse getUploadBoardOfFree(User user, FreeBoard freeBoard) {
        return UploadBoardResponse.builder()
                .category("projects")
                .id(freeBoard.getId())
                .title(freeBoard.getTitle())
                .content(freeBoard.getContent())
                .createdAt(freeBoard.getCreateAt())
                .commentNum(freeBoard.getComments().size())
                .like(isFreeBoardLiked(user.getId(), freeBoard.getLikes()))
                .likeNum(freeBoard.getLikes().size())
                .views(freeBoard.getViews())
                .imgUrl(freeBoard.getImgUrl())
                .build();
    }

    private static Boolean isFreeBoardLiked(Long id, Set<Like> likes) {
        if(likes!=null && !likes.isEmpty()){
            for (Like like : likes) {
                if(like.getUser().getId()==id) return true;
            }
        }
        return false;
    }
}
