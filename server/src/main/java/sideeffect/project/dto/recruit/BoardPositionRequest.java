package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.position.PositionType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BoardPositionRequest {

    private PositionType positionType;
    private int targetNumber;
}
