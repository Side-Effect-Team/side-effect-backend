package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class UserJoinRequest {
    private String email;
    private String nickname;
    private String password;

    public User toUser(){
        return User.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .build();
    }

}
