package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackLevelType;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserStack;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserStackRequest {

    private StackType stackType;
    private StackLevelType stackLevelType;

    public UserStack toUserStack(User user, Stack stack){
        return UserStack.builder()
                .stackLevelType(stackLevelType)
                .user(user)
                .stack(stack)
                .build();
    }
}
