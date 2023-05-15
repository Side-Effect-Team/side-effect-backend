package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.StackType;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardUpdateRequest {

    private String title;
    private String projectName;
    private String content;
    private String imgSrc;
    private List<StackType> tags;

    public RecruitBoard toRecruitBoard() {
        return RecruitBoard.builder()
                .title(title)
                .projectName(projectName)
                .contents(content)
                .build();
    }

}
