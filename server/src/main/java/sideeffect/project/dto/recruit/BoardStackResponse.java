package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.BoardStack;
import sideeffect.project.domain.stack.StackLevelType;
import sideeffect.project.domain.stack.StackType;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BoardStackResponse {

    private StackType stackType;
    private StackLevelType stackLevelType;
    private String url;

    public static BoardStackResponse of(BoardStack boardStack) {
        return BoardStackResponse.builder()
                .stackType(boardStack.getStack().getStackType())
                .stackLevelType(boardStack.getStackLevelType())
                .url(boardStack.getStack().getUrl())
                .build();
    }

    public static List<BoardStackResponse> listOf(List<BoardStack> boardStacks) {
        return boardStacks.stream()
                .map(BoardStackResponse::of)
                .collect(Collectors.toList());
    }
}

