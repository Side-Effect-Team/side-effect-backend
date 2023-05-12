package sideeffect.project.dto.recruit;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardAllResponse {
    private List<RecruitBoardResponse> recruitBoards;

    public static RecruitBoardAllResponse of(List<RecruitBoardResponse> recruitBoards) {
        return RecruitBoardAllResponse.builder()
                .recruitBoards(recruitBoards)
                .build();
    }
}
