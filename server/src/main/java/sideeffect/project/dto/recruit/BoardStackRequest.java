package sideeffect.project.dto.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.stack.StackLevelType;
import sideeffect.project.domain.stack.StackType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardStackRequest {

    private StackType stackType;
    private StackLevelType stackLevelType;
}

