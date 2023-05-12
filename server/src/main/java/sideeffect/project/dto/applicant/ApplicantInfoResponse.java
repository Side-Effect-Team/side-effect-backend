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
    private String email;
    private LocalDateTime createdAt;

    public static ApplicantInfoResponse of(ApplicantListResponse response) {
        return ApplicantInfoResponse.builder()
                .userId(response.getUserId())
                .applicantId(response.getApplicantId())
                .nickName(response.getNickName())
                .email(response.getEmail())
                .createdAt(response.getCreatedAt())
                .build();
    }
}
