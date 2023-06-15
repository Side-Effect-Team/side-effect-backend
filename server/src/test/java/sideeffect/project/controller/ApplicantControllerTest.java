package sideeffect.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.InvalidValueException;
import sideeffect.project.common.security.WithCustomUser;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.applicant.ApplicantStatus;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.dto.applicant.*;
import sideeffect.project.security.UserDetailsServiceImpl;
import sideeffect.project.service.ApplicantService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicantController.class)
@ExtendWith(RestDocumentationExtension.class)
class ApplicantControllerTest {

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private ApplicantService applicantService;

    private MockMvc mvc;
    private RecruitBoard recruitBoard;
    private BoardPosition boardPosition;
    private Applicant applicant;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentationContextProvider) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();

        applicant = Applicant.builder()
                .id(1L)
                .build();

        recruitBoard = RecruitBoard.builder()
                .id(1L)
                .title("모집 게시판")
                .contents("모집합니다.")
                .build();

        boardPosition = BoardPosition.builder()
                .id(1L)
                .targetNumber(3)
                .build();

        objectMapper = new ObjectMapper();
    }

    @DisplayName("게시판에 지원한다.")
    @WithCustomUser
    @Test
    void registerApplicant() throws Exception {
        ApplicantResponse applicantResponse = ApplicantResponse.of(applicant);

        given(applicantService.register(any(), any())).willReturn(applicantResponse);

        mvc.perform(RestDocumentationRequestBuilders.post("/api/applicant/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(MockMvcRestDocumentation.document("applicant/register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("BoardPosition Id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("Applicant ID"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태")
                        )
                        ));
    }

    @DisplayName("자신의 게시글에는 지원을 할 수 없다.")
    @WithCustomUser
    @Test
    void registerIsOwnedByUser() throws Exception {
        doThrow(new AuthException(ErrorCode.APPLICANT_SELF_UNAUTHORIZED))
                .when(applicantService).register(any(), any());

        mvc.perform(post("/api/applicant/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @DisplayName("동일한 게시글에는 중복 지원을 할 수 없다.")
    @WithCustomUser
    @Test
    void registerIsDuplicateApplicant() throws Exception {
        doThrow(new AuthException(ErrorCode.APPLICANT_DUPLICATED))
                .when(applicantService).register(any(), any());

        mvc.perform(post("/api/applicant/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @DisplayName("모집 게시판의 지원자 리스트를 조회한다.")
    @WithCustomUser
    @Test
    void findApplicants() throws Exception {
        List<ApplicantListResponse> applicantListResponses = generateApplicantListResponse(List.of(PositionType.FRONTEND, PositionType.BACKEND), 3);

        given(applicantService.findApplicants(any(), any(), any())).willReturn(ApplicantPositionResponse.mapOf(applicantListResponses));

        mvc.perform(get("/api/applicant/list/1")
                .contentType(MediaType.APPLICATION_JSON)
                .param("status", String.valueOf(ApplicantStatus.PENDING)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.프론트엔드.applicants.length()").value(3))
                .andExpect(jsonPath("$.백엔드.applicants.length()").value(3))
                .andExpect(jsonPath("$.프론트엔드.size").value(3))
                .andExpect(jsonPath("$.백엔드.size").value(3))
                .andDo(print());
    }

    @DisplayName("모집 게시판의 지원자 리스트는 글 작성자가 아니라면 조회가 불가능하다.")
    @WithCustomUser
    @Test
    void findApplicantsByNonOwner() throws Exception {
        doThrow(new AuthException(ErrorCode.APPLICANT_UNAUTHORIZED))
                .when(applicantService).findApplicants(any(), any(),any());

        mvc.perform(get("/api/applicant/list/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", String.valueOf(ApplicantStatus.PENDING)))
                .andExpect(status().isForbidden());
    }

    @DisplayName("게시판의 포지션에 지원한 지원자를 승인한다.")
    @WithCustomUser
    @Test
    void approveApplicant() throws Exception {
        ApplicantUpdateRequest request = ApplicantUpdateRequest.builder()
                .recruitBoardId(recruitBoard.getId())
                .applicantId(applicant.getId())
                .status(ApplicantStatus.APPROVED)
                .build();

        mvc.perform(patch("/api/applicant")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("게시판의 포지션에 모두 모집이 되었다면 지원자를 승인할 수 없다.")
    @WithCustomUser
    @Test
    void approveApplicantFullPosition() throws Exception {
        ApplicantUpdateRequest request = ApplicantUpdateRequest.builder()
                .recruitBoardId(recruitBoard.getId())
                .applicantId(applicant.getId())
                .status(ApplicantStatus.APPROVED)
                .build();

        doThrow(new EntityNotFoundException(ErrorCode.BOARD_POSITION_FULL))
                .when(applicantService).approveApplicant(any(), any());

        mvc.perform(patch("/api/applicant")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @DisplayName("글 작성자가 아니라면 게시판의 포지션에 지원한 지원자를 승인할 수 없다.")
    @WithCustomUser
    @Test
    void approveApplicantByNonOwner() throws Exception {
        ApplicantUpdateRequest request = ApplicantUpdateRequest.builder()
                .recruitBoardId(recruitBoard.getId())
                .applicantId(applicant.getId())
                .status(ApplicantStatus.APPROVED)
                .build();

        doThrow(new AuthException(ErrorCode.APPLICANT_UNAUTHORIZED))
                .when(applicantService).approveApplicant(any(), any());

        mvc.perform(patch("/api/applicant")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @DisplayName("해당 지원자가 이미 팀원으로 합류가 되어 있다면 승인할 수 없다.")
    @WithCustomUser
    @Test
    void approveApplicantIfExists() throws Exception {
        ApplicantUpdateRequest request = ApplicantUpdateRequest.builder()
                .recruitBoardId(recruitBoard.getId())
                .applicantId(applicant.getId())
                .status(ApplicantStatus.APPROVED)
                .build();

        doThrow(new InvalidValueException(ErrorCode.APPLICANT_NOT_EXISTS))
                .when(applicantService).approveApplicant(any(), any());

        mvc.perform(patch("/api/applicant")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @DisplayName("게시판의 포지션에 지원한 지원자를 거절한다.")
    @WithCustomUser
    @Test
    void rejectApplicant() throws Exception {
        ApplicantUpdateRequest request = ApplicantUpdateRequest.builder()
                .recruitBoardId(recruitBoard.getId())
                .applicantId(applicant.getId())
                .status(ApplicantStatus.REJECTED)
                .build();

        mvc.perform(patch("/api/applicant")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("글 작성자가 아니라면 게시판의 포지션에 지원한 지원자를 거절할 수 없다.")
    @WithCustomUser
    @Test
    void rejectApplicantByOwner() throws Exception {
        ApplicantUpdateRequest request = ApplicantUpdateRequest.builder()
                .recruitBoardId(recruitBoard.getId())
                .applicantId(applicant.getId())
                .status(ApplicantStatus.REJECTED)
                .build();

        doThrow(new AuthException(ErrorCode.APPLICANT_UNAUTHORIZED))
                .when(applicantService).rejectApplicant(any(), any());

        mvc.perform(patch("/api/applicant")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @DisplayName("게시판의 포지션에 합류한 팀원를 방출한다.")
    @WithCustomUser
    @Test
    void releaseApplicant() throws Exception {
        ApplicantReleaseRequest request = ApplicantReleaseRequest.builder()
                .recruitBoardId(recruitBoard.getId())
                .applicantId(applicant.getId())
                .build();

        mvc.perform(patch("/api/applicant/release")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("글 작성자가 아니라면 게시판의 포지션에 합류한 팀원을 방출할 수 없다.")
    @WithCustomUser
    @Test
    void releaseApplicantByNonOwner() throws Exception {
        ApplicantReleaseRequest request = ApplicantReleaseRequest.builder()
                .recruitBoardId(recruitBoard.getId())
                .applicantId(applicant.getId())
                .build();

        doThrow(new AuthException(ErrorCode.APPLICANT_UNAUTHORIZED))
                .when(applicantService).releaseApplicant(any(), any());

        mvc.perform(patch("/api/applicant/release")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @DisplayName("해당 지원자가 팀원으로 합류가 되어 있지 않다면 방출할 수 없다.")
    @WithCustomUser
    @Test
    void releaseApplicantNotExists() throws Exception {
        ApplicantReleaseRequest request = ApplicantReleaseRequest.builder()
                .recruitBoardId(recruitBoard.getId())
                .applicantId(applicant.getId())
                .build();

        doThrow(new InvalidValueException(ErrorCode.APPLICANT_NOT_EXISTS))
                .when(applicantService).releaseApplicant(any(), any());

        mvc.perform(patch("/api/applicant/release")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @DisplayName("지원자가 지원 취소를 한다.")
    @WithCustomUser
    @Test
    void cancelApplicant() throws Exception {
        mvc.perform(delete("/api/applicant/1")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    private static List<ApplicantListResponse> generateApplicantListResponse(List<PositionType> positionTypes, int size) {
        List<ApplicantListResponse> applicantListResponses = new ArrayList<>();
        Long id = 1L;
        for (PositionType positionType : positionTypes) {
            for(int i = 0; i < size; i++) {
                ApplicantListResponse response = ApplicantListResponse.builder().userId(id).applicantId(id).nickName("test" + id).positionType(positionType).build();
                applicantListResponses.add(response);
                id++;
            }
        }

        return applicantListResponses;
    }

}
