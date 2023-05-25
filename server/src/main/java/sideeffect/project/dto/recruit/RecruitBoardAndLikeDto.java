package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.RecruitBoard;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardAndLikeDto {
    private RecruitBoard recruitBoard;
    private boolean like;
}
