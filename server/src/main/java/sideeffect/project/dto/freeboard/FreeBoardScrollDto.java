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
public class FreeBoardScrollDto {

    private Long lastId;
    private Integer size;
    private String keyword;
    private OrderType orderType;
}
