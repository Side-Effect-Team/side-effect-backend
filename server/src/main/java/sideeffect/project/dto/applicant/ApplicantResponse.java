package sideeffect.project.dto.applicant;

import lombok.*;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.applicant.ApplicantStatus;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApplicantResponse {

    private Long id;
    private ApplicantStatus status;

    public static ApplicantResponse of(Applicant applicant) {
        return ApplicantResponse.builder()
                .id(applicant.getId())
                .status(applicant.getStatus())
                .build();
    }

}
