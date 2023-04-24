package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.ProgressType;
import sideeffect.project.domain.recruit.RecruitBoardType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardResponse {

    private String title;
    private String contents;
    private int views;
    private RecruitBoardType recruitBoardType;
    private ProgressType progressType;
    private LocalDateTime deadline;
    private String expectedPeriod;
    private List<BoardPositionResponse> positions;
    private List<BoardStackResponse> stacks;
}
