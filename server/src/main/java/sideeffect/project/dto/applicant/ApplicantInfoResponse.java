package sideeffect.project.dto.applicant;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApplicantInfoResponse {

    private Long userId;
    private Long applicantId;
    private String nickName;
    private LocalDateTime createAt;

    public static ApplicantInfoResponse of(ApplicantListResponse response) {
        return ApplicantInfoResponse.builder()
                .userId(response.getUserId())
                .applicantId(response.getApplicantId())
                .nickName(response.getNickName())
                .createAt(response.getCreateAt())
                .build();
    }
}
