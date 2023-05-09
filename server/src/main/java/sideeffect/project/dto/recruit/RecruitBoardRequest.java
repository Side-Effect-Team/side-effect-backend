package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.ProgressType;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.recruit.RecruitBoardType;
import sideeffect.project.domain.stack.StackType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardRequest {

    private String title;
    private String projectName;
    private String contents;
    private RecruitBoardType recruitBoardType;
    private ProgressType progressType;
    private LocalDateTime deadline;
    private String expectedPeriod;
    private List<BoardPositionRequest> positions;
    private List<StackType> tags;

    public RecruitBoard toRecruitBoard() {
        return RecruitBoard.builder()
                .title(title)
                .projectName(projectName)
                .contents(contents)
                .recruitBoardType(recruitBoardType)
                .progressType(progressType)
                .expectedPeriod(expectedPeriod)
                .deadline(deadline)
                .build();
    }

}
