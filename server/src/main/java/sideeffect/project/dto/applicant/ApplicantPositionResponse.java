package sideeffect.project.dto.applicant;

import lombok.*;
import sideeffect.project.domain.position.PositionType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApplicantPositionResponse {
    private List<ApplicantInfoResponse> applicants;
    private int size;

    public static Map<PositionType, ApplicantPositionResponse> mapOf(List<ApplicantListResponse> listResponses) {
        return listResponses.stream()
                .collect(groupingBy(ApplicantListResponse::getPositionType,
                        mapping(ApplicantInfoResponse::of, toList())))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry :: getKey,
                        entry -> ApplicantPositionResponse.builder()
                                .applicants(entry.getValue())
                                .size(entry.getValue().size())
                                .build()
                ));
    }
}
