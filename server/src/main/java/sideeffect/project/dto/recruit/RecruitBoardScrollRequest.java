package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.stack.StackType;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardScrollRequest {

    private Long lastId;
    @NotNull
    private int size;
    private String keyword;
    private List<StackType> stackTypes;

}
