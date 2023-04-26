package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.BoardStack;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackLevelType;
import sideeffect.project.domain.stack.StackType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BoardStackRequest {

    private StackType stackType;
    private StackLevelType stackLevelType;

    public BoardStack toBoardStack(RecruitBoard recruitBoard, Stack stack) {
        return BoardStack.builder()
                .stackLevelType(stackLevelType)
                .recruitBoard(recruitBoard)
                .stack(stack)
                .build();
    }
}

