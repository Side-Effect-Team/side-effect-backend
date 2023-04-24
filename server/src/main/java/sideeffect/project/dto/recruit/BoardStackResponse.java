package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.stack.StackLevelType;
import sideeffect.project.domain.stack.StackType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BoardStackResponse {

    private StackType stackType;
    private StackLevelType stackLevelType;
}

