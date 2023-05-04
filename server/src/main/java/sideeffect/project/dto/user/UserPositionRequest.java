package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserPosition;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserPositionRequest {
    private PositionType positionType;
    private String careerYears;

    public UserPosition toUserPosition(User user, Position position){
        return UserPosition.builder()
                .user(user)
                .position(position)
                .careerYears(careerYears)
                .build();
    }
}
