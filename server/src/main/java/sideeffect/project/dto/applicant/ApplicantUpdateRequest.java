package sideeffect.project.dto.applicant;

import lombok.*;
import sideeffect.project.domain.applicant.ApplicantStatus;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApplicantUpdateRequest {

    @NotNull
    private Long recruitBoardId;

    @NotNull
    private Long applicantId;

    @NotNull
    private ApplicantStatus status;

}
