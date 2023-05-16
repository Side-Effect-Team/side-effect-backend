package sideeffect.project.dto.applicant;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApplicantReleaseRequest {

    @NotNull
    private Long recruitBoardId;

    @NotNull
    private Long applicantId;

}
