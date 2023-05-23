package sideeffect.project.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.like.Like;
import sideeffect.project.domain.like.RecruitLike;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private Boolean like;
    private int likeNum;
    private int views;
    private int commentNum;
    private String imgUrl;
    private List<StackType> tags;

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
                .content(recruitBoard.getContents())
                .createdAt(recruitBoard.getCreateAt())
                .like(true)
                .likeNum(recruitBoard.getRecruitLikes().size())
                .views(recruitBoard.getViews())
                .imgUrl(recruitBoard.getImgSrc())
                .tags(getStackType(recruitBoard.getBoardStacks()))
                .build();
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
