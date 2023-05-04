package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class UserResponse {
    private String email;
    private String nickname;
    private String blogUrl;
    private String githubUrl;
    private UserRoleType userRoleType;

    public static UserResponse of(User user){
        return UserResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .blogUrl(user.getBlogUrl())
                .githubUrl(user.getGithubUrl())
                .userRoleType(user.getUserRoleType())
                .build();
    }

}
