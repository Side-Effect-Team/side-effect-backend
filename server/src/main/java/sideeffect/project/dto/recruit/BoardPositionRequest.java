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
public class BoardPositionRequest {

    private PositionType positionType;
    private int targetNumber;
}
