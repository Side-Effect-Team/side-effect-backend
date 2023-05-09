package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.BoardStack;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BoardStackRequest {

    private StackType stackType;

    public BoardStack toBoardStack(RecruitBoard recruitBoard, Stack stack) {
        return BoardStack.builder()
                .recruitBoard(recruitBoard)
                .stack(stack)
                .build();
    }
}

