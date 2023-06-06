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
    private String career;
    private String imgUrl;
    private String githubUrl;
    private String email;
    private LocalDateTime createdAt;

    public static ApplicantInfoResponse of(ApplicantListResponse response) {
        return ApplicantInfoResponse.builder()
                .userId(response.getUserId())
                .applicantId(response.getApplicantId())
                .nickName(response.getNickName())
                .career(response.getCareer())
                .imgUrl(response.getImgUrl())
                .githubUrl(response.getGithubUrl())
                .email(response.getEmail())
                .createdAt(response.getCreatedAt())
                .build();
    }
}
