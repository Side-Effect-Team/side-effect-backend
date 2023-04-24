package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.position.PositionType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BoardPositionResponse {

    private PositionType positionType;
    private int targetNumber;
    private int currentNumber;
}
