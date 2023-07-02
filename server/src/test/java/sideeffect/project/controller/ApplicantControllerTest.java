package sideeffect.project.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
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
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
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
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("applicant/register",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("지원 API")
                                                .description("모집게시판의 포지션에 지원한다.")
                                                .requestHeaders(
                                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                                )
                                                .pathParameters(
                                                    parameterWithName("id").description("지원할 포지션 아이디")
                                                )
                                                .responseFields(
                                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("지원 아이디"),
                                                    fieldWithPath("status").type(JsonFieldType.STRING).description("상태")
                                                ).build())
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
        Map<String, ApplicantPositionResponse> response = ApplicantPositionResponse.mapOf(applicantListResponses);
        addMissingPositionKeys(response);
        given(applicantService.findApplicants(any(), any(), any())).willReturn(response);

        mvc.perform(RestDocumentationRequestBuilders.get("/api/applicant/list/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .param("status", String.valueOf(ApplicantStatus.PENDING))
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.프론트엔드.applicants.length()").value(3))
                .andExpect(jsonPath("$.백엔드.applicants.length()").value(3))
                .andExpect(jsonPath("$.디자이너.applicants.length()").value(0))
                .andExpect(jsonPath("$.데브옵스.applicants.length()").value(0))
                .andExpect(jsonPath("$.마케터.applicants.length()").value(0))
                .andExpect(jsonPath("$.['프로젝트 매니저']['applicants'].length()").value(0))
                .andExpect(jsonPath("$.프론트엔드.size").value(3))
                .andExpect(jsonPath("$.백엔드.size").value(3))
                .andExpect(jsonPath("$.디자이너.size").value(0))
                .andExpect(jsonPath("$.데브옵스.size").value(0))
                .andExpect(jsonPath("$.마케터.size").value(0))
                .andExpect(jsonPath("$.['프로젝트 매니저']['size']").value(0))
                .andDo(MockMvcRestDocumentationWrapper.document("applicant/list",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("지원 API")
                                                .description("지원자 또는 팀원 목록을 조회한다.")
                                                .requestHeaders(
                                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                                )
                                                .pathParameters(
                                                    parameterWithName("id").description("지원할 포지션 아이디")
                                                )
                                                .requestParameters(
                                                    parameterWithName("status").description("지원 상태")
                                                )
                                                .responseFields(
                                                    fieldWithPath("프론트엔드.applicants[]").type(JsonFieldType.ARRAY).description("프론트엔드 지원 리스트"),
                                                    fieldWithPath("프론트엔드.applicants[].userId").type(JsonFieldType.NUMBER).description("프론트엔드 지원 유저 아이디"),
                                                    fieldWithPath("프론트엔드.applicants[].applicantId").type(JsonFieldType.NUMBER).description("프론트엔드 지원 아이디"),
                                                    fieldWithPath("프론트엔드.applicants[].nickName").type(JsonFieldType.STRING).description("프론트엔드 지원자 닉네임"),
                                                    fieldWithPath("프론트엔드.applicants[].career").type(JsonFieldType.STRING).description("프론트엔드 지원자 경력"),
                                                    fieldWithPath("프론트엔드.applicants[].imgUrl").type(JsonFieldType.STRING).description("프론트엔드 지원자 프로필 이미지"),
                                                    fieldWithPath("프론트엔드.applicants[].githubUrl").type(JsonFieldType.STRING).description("프론트엔드 지원자 깃허브 주소"),
                                                    fieldWithPath("프론트엔드.applicants[].email").type(JsonFieldType.STRING).description("프론트엔드 지원자 이메일"),
                                                    fieldWithPath("프론트엔드.applicants[].createdAt").type(JsonFieldType.STRING).description("프론트엔드 지원 일자"),
                                                    fieldWithPath("프론트엔드.size").type(JsonFieldType.NUMBER).description("프론트엔드 지원 수"),
                                                    fieldWithPath("백엔드.applicants[]").type(JsonFieldType.ARRAY).description("백엔드 지원 리스트"),
                                                    fieldWithPath("백엔드.applicants[].userId").type(JsonFieldType.NUMBER).description("백엔드 지원 유저 아이디"),
                                                    fieldWithPath("백엔드.applicants[].applicantId").type(JsonFieldType.NUMBER).description("백엔드 지원 아이디"),
                                                    fieldWithPath("백엔드.applicants[].nickName").type(JsonFieldType.STRING).description("백엔드 지원자 닉네임"),
                                                    fieldWithPath("백엔드.applicants[].career").type(JsonFieldType.STRING).description("백엔드 지원자 경력"),
                                                    fieldWithPath("백엔드.applicants[].imgUrl").type(JsonFieldType.STRING).description("백엔드 지원자 프로필 이미지"),
                                                    fieldWithPath("백엔드.applicants[].githubUrl").type(JsonFieldType.STRING).description("백엔드 지원자 깃허브 주소"),
                                                    fieldWithPath("백엔드.applicants[].email").type(JsonFieldType.STRING).description("백엔드 지원자 이메일"),
                                                    fieldWithPath("백엔드.applicants[].createdAt").type(JsonFieldType.STRING).description("백엔드 지원 일자"),
                                                    fieldWithPath("백엔드.size").type(JsonFieldType.NUMBER).description("백엔드 지원 수"),
                                                    fieldWithPath("디자이너.applicants[]").type(JsonFieldType.ARRAY).description("디자이너 지원 리스트"),
                                                    fieldWithPath("디자이너.size").type(JsonFieldType.NUMBER).description("디자이너 지원 수"),
                                                    fieldWithPath("데브옵스.applicants[]").type(JsonFieldType.ARRAY).description("데브옵스 지원 리스트"),
                                                    fieldWithPath("데브옵스.size").type(JsonFieldType.NUMBER).description("데브옵스 지원 수"),
                                                    fieldWithPath("마케터.applicants[]").type(JsonFieldType.ARRAY).description("마케터 지원 리스트"),
                                                    fieldWithPath("마케터.size").type(JsonFieldType.NUMBER).description("마케터 지원 수"),
                                                    fieldWithPath("프로젝트 매니저.applicants[]").type(JsonFieldType.ARRAY).description("프로젝트 매니저 지원 리스트"),
                                                    fieldWithPath("프로젝트 매니저.size").type(JsonFieldType.NUMBER).description("프로젝트 매니저 지원 수")
                                                ).build())
                ));
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

        mvc.perform(RestDocumentationRequestBuilders.patch("/api/applicant")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("applicant/approve-reject",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("지원 API")
                                                .description("포지션에 지원한 지원자를 수락 또는 거절한다.")
                                                .requestHeaders(
                                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                                )
                                                .requestFields(
                                                    fieldWithPath("recruitBoardId").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                                    fieldWithPath("applicantId").type(JsonFieldType.NUMBER).description("지원 아이디"),
                                                    fieldWithPath("status").type(JsonFieldType.STRING).description("상태(approved = 수락, rejected = 거절)")
                                                ).build())
                ));
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

        mvc.perform(RestDocumentationRequestBuilders.patch("/api/applicant/release")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("applicant/release",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("지원 API")
                                                .description("프로젝트에 참여한 팀원을 방출한다.")
                                                .requestHeaders(
                                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                                )
                                                .requestFields(
                                                    fieldWithPath("recruitBoardId").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                                    fieldWithPath("applicantId").type(JsonFieldType.NUMBER).description("지원 아이디")
                                                ).build())
                ));
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
        mvc.perform(RestDocumentationRequestBuilders.delete("/api/applicant/{id}", 1L)
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("applicant/cancel",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("지원 API")
                                                .description("지원을 취소한다.")
                                                .requestHeaders(
                                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                                )
                                                .pathParameters(
                                                    parameterWithName("id").description("모집 포지션 아이디")
                                                ).build())
                ));
    }

    private static List<ApplicantListResponse> generateApplicantListResponse(List<PositionType> positionTypes, int size) {
        List<ApplicantListResponse> applicantListResponses = new ArrayList<>();
        Long id = 1L;
        for (PositionType positionType : positionTypes) {
            for(int i = 0; i < size; i++) {
                ApplicantListResponse response = ApplicantListResponse.builder().userId(id).applicantId(id).nickName("test" + id).positionType(positionType)
                        .career("junior").imgUrl("profile.img").githubUrl("github.com").email("tester@naver.com").createdAt(LocalDateTime.now()).build();
                applicantListResponses.add(response);
                id++;
            }
        }

        return applicantListResponses;
    }

    private void addMissingPositionKeys(Map<String, ApplicantPositionResponse> maps) {
        for (PositionType positionType : PositionType.values()) {
            if(!maps.containsKey(positionType.getKoreanName())) {
                ApplicantPositionResponse response = ApplicantPositionResponse.builder().applicants(new ArrayList<>()).size(0).build();
                maps.put(positionType.getKoreanName(), response);
            }
        }
    }

}
