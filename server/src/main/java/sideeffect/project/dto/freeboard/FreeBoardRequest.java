package sideeffect.project.dto.freeboard;

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
public class FreeBoardRequest {

    private String title;
    private String projectUrl;
    private String content;

    public FreeBoard toFreeBoard() {
        return FreeBoard.builder()
            .title(title)
            .content(content)
            .projectUrl(projectUrl)
            .build();
    }
}
