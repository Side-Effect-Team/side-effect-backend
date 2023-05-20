package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.StackType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardRequest {

    @NotBlank
    @Size(min = 5, message = "제목은 5글자 이상이어야 합니다.")
    private String title;

    @NotBlank
    @Size(min = 3, max = 20, message = "프로젝트명은 3~20글자 이어야 합니다.")
    private String projectName;

    @NotBlank
    @Size(min = 20, message = "내용은 20글자 이상이어야 합니다.")
    private String content;

    private List<BoardPositionRequest> positions;

    private List<StackType> tags;

    public RecruitBoard toRecruitBoard() {
        return RecruitBoard.builder()
                .title(title)
                .projectName(projectName)
                .contents(content)
                .build();
    }

}
