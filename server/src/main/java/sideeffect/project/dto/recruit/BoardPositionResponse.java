package sideeffect.project.dto.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.position.PositionType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardPositionResponse {

    private PositionType positionType;
    private int targetNumber;
    private int currentNumber;
}
