package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.applicant.ApplicantStatus;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.user.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class ApplyBoardResponse {
    //
    private String category;
    private Long positionId;
    private Long boardId;
    private String title;
    private PositionType position;
    private ApplicantStatus status;
    private Boolean closed;

    public static List<ApplyBoardResponse> listOf(User user){

        List<ApplyBoardResponse> applyBoardResponseList = Collections.emptyList();
        List<Applicant> applicants = user.getApplicants();
        if(applicants!=null && !applicants.isEmpty()) {
            applyBoardResponseList = applicants.stream()
                    .map(applicant -> getApplyBoardResponse(user, applicant))
                    .collect(Collectors.toList());
        }

        return applyBoardResponseList;
    }

    private static ApplyBoardResponse getApplyBoardResponse(User user, Applicant applicant) {
        return ApplyBoardResponse.builder()
                .category("recruits")
                .positionId(applicant.getBoardPosition().getId())
                .boardId(applicant.getBoardPosition().getRecruitBoard().getId())
                .title(applicant.getBoardPosition().getRecruitBoard().getTitle())
                .position(applicant.getBoardPosition().getPosition().getPositionType())
                .status(applicant.getStatus())
                .closed(applicant.getBoardPosition().getTargetNumber()==applicant.getBoardPosition().getCurrentNumber())
                .build();
    }

}
