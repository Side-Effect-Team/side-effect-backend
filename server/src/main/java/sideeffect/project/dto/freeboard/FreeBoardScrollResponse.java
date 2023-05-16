package sideeffect.project.dto.freeboard;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FreeBoardScrollResponse {
    private List<FreeBoardResponse> projects;
    private Long lastId;
    private boolean hasNext;

    public static FreeBoardScrollResponse of(List<FreeBoardResponse> freeBoards, boolean hasNext) {
        if (freeBoards.isEmpty()) {
            return FreeBoardScrollResponse.builder()
                .projects(freeBoards)
                .hasNext(hasNext)
                .build();
        }

        return FreeBoardScrollResponse.builder()
            .projects(freeBoards)
            .lastId(freeBoards.get(freeBoards.size() - 1).getId())
            .hasNext(hasNext)
            .build();
    }
}
