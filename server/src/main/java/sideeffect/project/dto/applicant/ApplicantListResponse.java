package sideeffect.project.dto.applicant;

import lombok.*;
import sideeffect.project.domain.position.PositionType;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApplicantListResponse {

    private Long userId;
    private Long applicantId;
    private String nickName;
    private String career;
    private String imgUrl;
    private String githubUrl;
    private String email;
    private PositionType positionType;
    private LocalDateTime createdAt;

    public String getPositionTypeByKoreanName() {
        return positionType.getKoreanName();
    }

}
