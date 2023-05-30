package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.BoardPosition;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DetailedBoardPositionResponse {

    private Long id;
    private String positionType;
    private int targetNumber;
    private int currentNumber;
    private boolean supported;

    public static DetailedBoardPositionResponse of(BoardPosition boardPosition) {
        return DetailedBoardPositionResponse.builder()
                .id(boardPosition.getId())
                .positionType(boardPosition.getPosition().getPositionType().getValue())
                .targetNumber(boardPosition.getTargetNumber())
                .currentNumber(boardPosition.getCurrentNumber())
                .build();
    }

    public static List<DetailedBoardPositionResponse> listOf(List<BoardPosition> boardPositions) {
        return boardPositions.stream()
                .map(DetailedBoardPositionResponse::of)
                .collect(Collectors.toList());
    }

    public void updateSupported() {
        this.supported = true;
    }

}
