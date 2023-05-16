package sideeffect.project.dto.recruit;

import lombok.*;
import sideeffect.project.domain.recruit.BoardPosition;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BoardPositionResponse {

    private Long id;
    private String positionType;
    private int targetNumber;
    private int currentNumber;

    public static BoardPositionResponse of(BoardPosition boardPosition) {
        return BoardPositionResponse.builder()
                .id(boardPosition.getId())
                .positionType(boardPosition.getPosition().getPositionType().getValue())
                .targetNumber(boardPosition.getTargetNumber())
                .currentNumber(boardPosition.getCurrentNumber())
                .build();
    }

    public static List<BoardPositionResponse> listOf(List<BoardPosition> boardPositions) {
        return boardPositions.stream()
                .map(BoardPositionResponse::of)
                .collect(Collectors.toList());
    }
}
