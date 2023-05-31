package sideeffect.project.dto.recruit;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardAllResponse {
    private List<RecruitBoardListResponse> recruitBoards;

    public static RecruitBoardAllResponse of(List<RecruitBoardListResponse> recruitBoards) {
        return RecruitBoardAllResponse.builder()
                .recruitBoards(recruitBoards)
                .build();
    }
}
