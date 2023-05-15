package sideeffect.project.dto.freeboard;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FreeBoardScrollRequest {
    private Long lastId;
    private Integer size;
    private OrderType orderType;

    public FreeBoardScrollDto toScrollDto() {
        return FreeBoardScrollDto.builder()
            .lastId(lastId)
            .size(size)
            .orderType(orderType)
            .build();
    }

    public FreeBoardScrollDto toScrollDtoWithoutLastId() {
        return FreeBoardScrollDto.builder()
            .size(size)
            .orderType(orderType)
            .build();
    }
}
