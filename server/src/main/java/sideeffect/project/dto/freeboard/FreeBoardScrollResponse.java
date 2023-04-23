package sideeffect.project.dto.freeboard;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.freeboard.FreeBoard;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FreeBoardScrollResponse {
    private List<FreeBoard> freeBoards;
    private Long lastId;
    private boolean hasNext;

    public static FreeBoardScrollResponse of(List<FreeBoard> freeBoards, boolean hasNext) {
        return FreeBoardScrollResponse.builder()
            .freeBoards(freeBoards)
            .lastId(freeBoards.get(freeBoards.size() - 1).getId())
            .hasNext(hasNext)
            .build();
    }
}
