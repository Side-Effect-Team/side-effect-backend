package sideeffect.project.dto.applicant;

import lombok.*;
import sideeffect.project.domain.applicant.Applicant;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApplicantRequest {

    private Long recruitBoardId;
    private Long boardPositionId;

    public Applicant toApplicant() {
        return Applicant.builder().build();
    }

}
