package sideeffect.project.dto.applicant;

import lombok.*;
import sideeffect.project.domain.applicant.Applicant;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApplicantRequest {

    @NotNull
    private Long recruitBoardId;

    @NotNull
    private Long boardPositionId;

    public Applicant toApplicant() {
        return Applicant.builder().build();
    }

}
