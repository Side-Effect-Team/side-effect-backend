package sideeffect.project.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.like.RecruitLike;
import sideeffect.project.domain.recruit.BoardStack;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class ApplyBoardResponse {

    private String category;
    private Long id;
    private String title;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private Boolean like;
    private int likeNum;
    private int view;
    private int commentNum;
    private String imgUrl;
    private List<StackType> tags;

    public static List<ApplyBoardResponse> listOf(User user){

        List<ApplyBoardResponse> applyBoardResponseList = Collections.emptyList();
        List<Applicant> applicants = user.getApplicants();
        if(applicants!=null && !applicants.isEmpty()) {
            applyBoardResponseList = applicants.stream()
                    .map(applicant -> getApplyBoardResponse(user, applicant.getBoardPosition().getRecruitBoard()))
                    .collect(Collectors.toList());
        }

        return applyBoardResponseList;
    }

    private static ApplyBoardResponse getApplyBoardResponse(User user, RecruitBoard recruitBoard) {
        return ApplyBoardResponse.builder()
                .category("recruits")
                .id(recruitBoard.getId())
                .title(recruitBoard.getTitle())
                .content(recruitBoard.getContents())
                .createdAt(recruitBoard.getCreateAt())
                .like(isLiked(user.getId(), recruitBoard.getRecruitLikes()))
                .likeNum(recruitBoard.getRecruitLikes().size())
                .view(recruitBoard.getViews())
                .imgUrl(recruitBoard.getImgSrc())
                .tags(getStackTypes(recruitBoard.getBoardStacks()))
                .build();
    }

    private static Boolean isLiked(Long id, List<RecruitLike> recruitLikes) {
        if(recruitLikes!=null && !recruitLikes.isEmpty()){
            for (RecruitLike recruitLike : recruitLikes) {
                if(recruitLike.getUser().getId()==id) return true;
            }
        }
        return false;
    }

    private static List<StackType> getStackTypes(List<BoardStack> boardStacks) {
        List<StackType> stackTypes = Collections.emptyList();
        if(boardStacks!=null && !boardStacks.isEmpty()){
            boardStacks.stream()
                    .map(boardStack -> boardStack.getStack().getStackType())
                    .collect(Collectors.toList());
        }
        return stackTypes;
    }
}
