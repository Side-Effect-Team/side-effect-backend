package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class UserRequest {
    private String email;
    private String nickname;
    private String password;
    private List<UserPositionRequest> positions;
    private List<UserStackRequest> stacks;
    private String blogUrl;
    private String githubUrl;
    private String imgUrl;

    public User toUser(){
        return User.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .blogUrl(blogUrl)
                .githubUrl(githubUrl)
                .imgUrl(imgUrl)
                .build();
    }

}
