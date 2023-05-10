package sideeffect.project.dto.recruit;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecruitBoardScrollResponse {

    private List<RecruitBoardResponse> recruitBoards;
    private Long lastId;
    private boolean hasNext;

    public static RecruitBoardScrollResponse of(List<RecruitBoardResponse> recruitBoards, boolean hasNext) {
        if (recruitBoards.isEmpty()) {
            return RecruitBoardScrollResponse.builder()
                    .recruitBoards(recruitBoards)
                    .hasNext(hasNext)
                    .build();
        }

        return RecruitBoardScrollResponse.builder()
                .recruitBoards(recruitBoards)
                .lastId(recruitBoards.get(recruitBoards.size() - 1).getId())
                .hasNext(hasNext)
                .build();
    }

}
