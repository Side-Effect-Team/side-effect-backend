package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.RecruitBoard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardResponse {

    private Long id;
    private String title;
    private String content;
    private int views;
    private String recruitBoardType;
    private String progressType;
    private LocalDateTime deadline;
    private String expectedPeriod;
    private List<BoardPositionResponse> positions;
    private List<BoardStackResponse> tags;

    public static RecruitBoardResponse of(RecruitBoard recruitBoard) {
        return RecruitBoardResponse.builder()
                .id(recruitBoard.getId())
                .title(recruitBoard.getTitle())
                .content(recruitBoard.getContents())
                .views(recruitBoard.getViews())
                .recruitBoardType(recruitBoard.getRecruitBoardType().getValue())
                .progressType(recruitBoard.getProgressType().getValue())
                .deadline(recruitBoard.getDeadline())
                .expectedPeriod(recruitBoard.getExpectedPeriod())
                .positions(BoardPositionResponse.listOf(recruitBoard.getBoardPositions()))
                .tags(BoardStackResponse.listOf(recruitBoard.getBoardStacks()))
                .build();
    }

    public static List<RecruitBoardResponse> listOf(List<RecruitBoard> recruitBoards) {
        return recruitBoards.stream()
                .map(RecruitBoardResponse::of)
                .collect(Collectors.toList());
    }

}
