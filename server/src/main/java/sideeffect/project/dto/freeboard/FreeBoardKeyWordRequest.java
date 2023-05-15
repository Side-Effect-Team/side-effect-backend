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
public class FreeBoardKeyWordRequest {
    private Long lastId;

    private Integer size;

    private String keyword;

    private OrderType orderType;

    public FreeBoardScrollDto toScrollDto() {
        return FreeBoardScrollDto.builder()
            .lastId(lastId)
            .size(size)
            .keyword(keyword)
            .orderType(orderType)
            .build();
    }

    public FreeBoardScrollDto toScrollDtoWithoutLastId() {
        return FreeBoardScrollDto.builder()
            .size(size)
            .keyword(keyword)
            .orderType(orderType)
            .build();
    }
}
