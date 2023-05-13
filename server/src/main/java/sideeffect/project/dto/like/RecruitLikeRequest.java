package sideeffect.project.dto.like;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitLikeRequest {
    private Long userId;
    private Long recruitBoardId;
}
