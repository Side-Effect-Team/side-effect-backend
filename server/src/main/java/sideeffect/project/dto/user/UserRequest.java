package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.user.ProviderType;
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
    private String introduction;
    private PositionType position;
    private String career;
    private List<String> tags;
    private ProviderType providerType;
    private String blogUrl;
    private String githubUrl;
    private String imgUrl;
    private String portfolioUrl;

    public User toUser(){
        return User.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .career(career)
                .position(position)
                .providerType(providerType)
                .blogUrl(blogUrl)
                .githubUrl(githubUrl)
                .imgUrl(imgUrl)
                .introduction(introduction)
                .portfolioUrl(portfolioUrl)
                .build();
    }

}
