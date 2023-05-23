package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class UserEditResponse {

    private String nickname;
    private String introduction;
    private PositionType position;
    private String career;
    private List<String> tags;
    private String imgUrl;
    private String blogUrl;
    private String githubUrl;
    private String portfolioUrl;

    public static UserEditResponse of(User user){
        return UserEditResponse.builder()
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .position(user.getPosition())
                .career(user.getCareer())
                .tags(getStackType(user.getUserStacks()))
                .imgUrl(user.getImgUrl())
                .blogUrl(user.getBlogUrl())
                .githubUrl(user.getGithubUrl())
                .portfolioUrl(user.getPortfolioUrl())
                .build();
    }

    private static List<String> getStackType(List<UserStack> userStacks) {
        List<String> stackTypes = Collections.emptyList();
        if(userStacks!=null && !userStacks.isEmpty()){
            stackTypes = userStacks.stream()
                    .map(userStack -> userStack.getStack())
                    .collect(Collectors.toList());
        }
        return stackTypes;
    }

}
