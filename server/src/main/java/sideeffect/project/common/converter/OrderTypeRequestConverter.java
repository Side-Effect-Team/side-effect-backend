package sideeffect.project.common.converter;

import static org.springframework.util.StringUtils.hasText;
import static sideeffect.project.dto.freeboard.OrderType.LATEST;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sideeffect.project.dto.freeboard.OrderType;

@Component
public class OrderTypeRequestConverter implements Converter<String, OrderType> {

    @Override
    public OrderType convert(String source) {
        if (!hasText(source)) {
            return LATEST;
        }
        return OrderType.parse(source);
    }
}
