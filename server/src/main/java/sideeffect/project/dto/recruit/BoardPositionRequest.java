package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.RecruitBoard;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BoardPositionRequest {

    private PositionType positionType;
    private int targetNumber;

    public BoardPosition toBoardPosition(RecruitBoard recruitBoard, Position position) {
        return BoardPosition.builder()
                .targetNumber(targetNumber)
                .recruitBoard(recruitBoard)
                .position(position)
                .build();
    }
}
