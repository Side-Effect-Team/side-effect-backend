package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserStack;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String introduction;
    private int boards;
    private PositionType position;
    private String career;
    private List<String> tags;
    private String imgUrl;
    private String blogUrl;
    private String githubUrl;
    private String portfolioUrl;
    private List<LikeBoardResponse> likeBoards;
    private List<UploadBoardResponse> uploadBoards;
    private List<ApplyBoardResponse> applyBoards;

    private Boolean isOwner;

    public static UserResponse ownerOf(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .boards(getBoards(user))
                .position(user.getPosition())
                .career(user.getCareer())
                .tags(listOf(user.getUserStacks()))
                .imgUrl(user.getImgUrl())
                .blogUrl(user.getBlogUrl())
                .githubUrl(user.getGithubUrl())
                .portfolioUrl(user.getPortfolioUrl())
                .likeBoards(LikeBoardResponse.listOf(user))
                .uploadBoards(UploadBoardResponse.listOf(user))
                .applyBoards(ApplyBoardResponse.listOf(user))
                .build();
    }

    public static UserResponse justOf(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .boards(getBoards(user))
                .position(user.getPosition())
                .career(user.getCareer())
                .tags(listOf(user.getUserStacks()))
                .imgUrl(user.getImgUrl())
                .blogUrl(user.getBlogUrl())
                .githubUrl(user.getGithubUrl())
                .portfolioUrl(user.getPortfolioUrl())
                .build();
    }
    private static int getBoards(User user) {
        int boards = 0;
        if(user.getFreeBoards()!=null && !user.getFreeBoards().isEmpty())
            boards+=user.getFreeBoards().size();
        if(user.getRecruitBoards()!=null && !user.getRecruitBoards().isEmpty())
            boards+=user.getRecruitBoards().size();
        return boards;
    }

    private static List<String> listOf(List<UserStack> userStacks) {
        return userStacks.stream()
                .map(userStack -> userStack.getStack())
                .collect(Collectors.toList());
    }

    public void setIsOwner(boolean flag){
        this.isOwner = flag;
    }
}
