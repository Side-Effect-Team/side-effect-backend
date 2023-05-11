package sideeffect.project.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sideeffect.project.domain.applicant.ApplicantStatus;

@Component
public class ApplicantStatusRequestConverter implements Converter<String, ApplicantStatus> {

    @Override
    public ApplicantStatus convert(String source) {
        return ApplicantStatus.of(source);
    }
}
