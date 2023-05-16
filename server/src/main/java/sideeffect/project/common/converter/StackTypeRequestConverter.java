package sideeffect.project.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sideeffect.project.domain.stack.StackType;

@Component
public class StackTypeRequestConverter implements Converter<String, StackType> {

    @Override
    public StackType convert(String source) {
        return StackType.of(source);
    }
}
