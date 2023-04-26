package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.ProgressType;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.recruit.RecruitBoardType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardResponse {

    private Long id;
    private String title;
    private String contents;
    private int views;
    private RecruitBoardType recruitBoardType;
    private ProgressType progressType;
    private LocalDateTime deadline;
    private String expectedPeriod;
    private List<BoardPositionResponse> positions;
    private List<BoardStackResponse> stacks;

    public static RecruitBoardResponse of(RecruitBoard recruitBoard) {
        return RecruitBoardResponse.builder()
                .id(recruitBoard.getId())
                .title(recruitBoard.getTitle())
                .contents(recruitBoard.getContents())
                .views(recruitBoard.getViews())
                .recruitBoardType(recruitBoard.getRecruitBoardType())
                .progressType(recruitBoard.getProgressType())
                .deadline(recruitBoard.getDeadline())
                .expectedPeriod(recruitBoard.getExpectedPeriod())
                .positions(BoardPositionResponse.listOf(recruitBoard.getBoardPositions()))
                .stacks(BoardStackResponse.listOf(recruitBoard.getBoardStacks()))
                .build();
    }

}
