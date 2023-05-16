package sideeffect.project.domain.applicant;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum ApplicantStatus {
    PENDING("pending"), APPROVED("approved"), REJECTED("rejected");

    private final String value;

    public String getValue() {
        return value;
    }

    ApplicantStatus(String value) {
        this.value = value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ApplicantStatus parsing(String value) {
        return Stream.of(ApplicantStatus.values())
                .filter(applicantStatus -> applicantStatus.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

    public static ApplicantStatus of(String value) {
        return Stream.of(ApplicantStatus.values())
                .filter(applicantStatus -> applicantStatus.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
