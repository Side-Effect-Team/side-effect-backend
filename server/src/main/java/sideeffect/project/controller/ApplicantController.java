package sideeffect.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sideeffect.project.domain.applicant.ApplicantStatus;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.applicant.*;
import sideeffect.project.security.LoginUser;
import sideeffect.project.service.ApplicantService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applicant")
public class ApplicantController {

    private final ApplicantService applicantService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ApplicantResponse registerApplicant(@LoginUser User user,@Valid @RequestBody ApplicantRequest request) {
        return applicantService.register(user, request);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("list/{boardId}")
    public Map<PositionType, ApplicantPositionResponse> findApplicants(
            @LoginUser User user,
            @PathVariable Long boardId,
            @RequestParam(value = "status") ApplicantStatus status
    ) {
        return applicantService.findApplicants(user.getId(), boardId, status);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping
    public void updateApplicant(@LoginUser User user, @Valid @RequestBody ApplicantUpdateRequest request) {
        if(request.getStatus().equals(ApplicantStatus.APPROVED)) {
            applicantService.approveApplicant(user.getId(), request);
        }else if(request.getStatus().equals(ApplicantStatus.REJECTED)) {
            applicantService.rejectApplicant(user.getId(), request);
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/release")
    public void releaseApplicant(@LoginUser User user, @Valid @RequestBody ApplicantReleaseRequest request) {
        applicantService.releaseApplicant(user.getId(), request);
    }

}