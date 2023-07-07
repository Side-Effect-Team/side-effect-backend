package sideeffect.project.controller;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sideeffect.project.common.security.WithCustomUser;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.token.RefreshToken;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.user.UserEditResponse;
import sideeffect.project.dto.user.UserRequest;
import sideeffect.project.dto.user.UserResponse;
import sideeffect.project.security.RefreshTokenProvider;
import sideeffect.project.service.UserService;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@WebMvcTest(UserController.class)
@ExtendWith(RestDocumentationExtension.class)
class UserControllerTest {


    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    RefreshTokenProvider refreshTokenProvider;

    ObjectMapper objectMapper;

    User user;
    UserRequest userRequest;
    UserResponse userResponse;

    @BeforeEach
    void beforeEach(WebApplicationContext context, RestDocumentationContextProvider restDocumentationContextProvider){
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
        user = User.builder()
                .email("google@google.com")
                .password("1234")
                .nickname("ABC")
                .introduction("안녕하세요")
                .position(PositionType.BACKEND)
                .career("junior")
                .providerType(ProviderType.GOOGLE)
                .blogUrl("tistory/tlsrl6427")
                .githubUrl("github/tlsrl6427")
                .portfolioUrl("naver.com/tlsrl6427")
                .userRoleType(UserRoleType.ROLE_USER)
                .build();

        userRequest = UserRequest.builder()
                .email("google@google.com")
                .password("1234")
                .nickname("ABC")
                .introduction("안녕하세요")
                .position(PositionType.BACKEND)
                .career("junior")
                .tags(List.of("JAVA", "SPRING"))
                .providerType(ProviderType.GOOGLE)
                .blogUrl("tistory/tlsrl6427")
                .githubUrl("github/tlsrl6427")
                .portfolioUrl("naver.com/tlsrl6427")
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .email("google@google.com")
                .nickname("ABC")
                .introduction("안녕하세요")
                .boards(1)
                .position(PositionType.BACKEND)
                .career("junior")
                .tags(List.of("JAVA", "SPRING"))
                .blogUrl("tistory/tlsrl6427")
                .githubUrl("github/tlsrl6427")
                .portfolioUrl("naver.com/tlsrl6427")
                .isOwner(true)
                .build();
    }
    @Test
    @DisplayName("회원가입")
    @WithCustomUser
    void join() throws Exception {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(1L)
                .refreshToken("test")
                .build();

        doReturn(user).when(userService).join(any());
        doReturn(refreshToken).when(refreshTokenProvider).createRefreshToken(any());
        doReturn("accessToken").when(refreshTokenProvider).issueAccessToken(any());

        mockMvc.perform(post("/api/user/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        //.with(anonymous())
                        .content(objectMapper.writeValueAsBytes(userRequest)))
                .andDo(print())
                .andDo(document("user/join",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User")
                                        .summary("회원가입")
                                        .description("신규 유저를 회원가입한다")
                                        .requestHeaders(
                                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                                fieldWithPath("introduction").type(JsonFieldType.STRING).description("자기소개"),
                                                fieldWithPath("boards").type(JsonFieldType.STRING).description("보유 게시글 수"),
                                                fieldWithPath("position").type(JsonFieldType.STRING).description("포지션"),
                                                fieldWithPath("career").type(JsonFieldType.STRING).description("경력"),
                                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("기술 태그"),
                                                fieldWithPath("providerType").type(JsonFieldType.STRING).description("소셜 유형"),
                                                fieldWithPath("blogUrl").type(JsonFieldType.STRING).description("블로그 URL"),
                                                fieldWithPath("githubUrl").type(JsonFieldType.STRING).description("깃허브 URL"),
                                                fieldWithPath("portfolioUrl").type(JsonFieldType.STRING).description("포트폴리오 URL"),
                                                fieldWithPath("isOwner").type(JsonFieldType.BOOLEAN).description("본인여부")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("유저 아이디")
                                        )
                                        .build()
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("userId").value(refreshToken.getUserId()));

        assertAll(
                () -> verify(userService).join(any()),
                () -> verify(refreshTokenProvider).createRefreshToken(any()),
                () -> verify(refreshTokenProvider).issueAccessToken(any())
        );
    }

    @Test
    @DisplayName("단건조회")
    @WithCustomUser
    void view() throws Exception {
        when(userService.findOne(any(), any())).thenReturn(userResponse);
        mockMvc.perform(get("/api/user/mypage")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value(userResponse.getEmail()))
                .andExpect(jsonPath("nickname").value(userResponse.getNickname()))
                .andExpect(jsonPath("blogUrl").value(userResponse.getBlogUrl()))
                .andExpect(jsonPath("githubUrl").value(userResponse.getGithubUrl()))
                .andDo(document("user/mypage",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User")
                                        .summary("단건 조회")
                                        .description("해당 아이디의 유저를 단건 조회한다")
                                        .pathParameters(
                                                parameterWithName("id").description("유저 아이디")
                                        )
                                        .requestHeaders(
                                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰").optional()
                                        )
                                        .responseFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                                fieldWithPath("introduction").type(JsonFieldType.STRING).description("자기소개"),
                                                fieldWithPath("position").type(JsonFieldType.STRING).description("포지션"),
                                                fieldWithPath("career").type(JsonFieldType.STRING).description("경력"),
                                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("기술 태그"),
                                                fieldWithPath("providerType").type(JsonFieldType.STRING).description("소셜 유형"),
                                                fieldWithPath("blogUrl").type(JsonFieldType.STRING).description("블로그 URL"),
                                                fieldWithPath("githubUrl").type(JsonFieldType.STRING).description("깃허브 URL"),
                                                fieldWithPath("portfolioUrl").type(JsonFieldType.STRING).description("포트폴리오 URL")
                                        )
                                        .build()
                        )
                ))
                .andDo(print());
        verify(userService).findOne(any(), any());
    }

    @DisplayName("수정페이지 조회")
    @Test
    @WithCustomUser
    void edit() throws Exception {
        UserEditResponse userEditResponse = UserEditResponse.builder()
                .nickname("ABC")
                .introduction("안녕하세요")
                .blogUrl("tistory/tlsrl6427")
                .githubUrl("github/tlsrl6427")
                .build();
        doReturn(userEditResponse).when(userService).findEditInfo(any());

        mockMvc.perform(get("/api/user/editpage")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("introduction").value(userEditResponse.getIntroduction()))
                .andExpect(jsonPath("nickname").value(userEditResponse.getNickname()))
                .andExpect(jsonPath("blogUrl").value(userEditResponse.getBlogUrl()))
                .andExpect(jsonPath("githubUrl").value(userEditResponse.getGithubUrl()))
                .andDo(print())
                .andDo(document("user/editpage",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User")
                                        .summary("수정페이지 조회")
                                        .description("해당 유저의 수정페이지를 조회한다")
                                        .requestHeaders(
                                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                                fieldWithPath("introduction").type(JsonFieldType.STRING).description("자기소개"),
                                                fieldWithPath("position").type(JsonFieldType.STRING).description("포지션"),
                                                fieldWithPath("career").type(JsonFieldType.STRING).description("경력"),
                                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("기술 태그"),
                                                fieldWithPath("imgUrl").type(JsonFieldType.STRING).description("이미지 URL"),
                                                fieldWithPath("blogUrl").type(JsonFieldType.STRING).description("블로그 URL"),
                                                fieldWithPath("githubUrl").type(JsonFieldType.STRING).description("깃허브 URL"),
                                                fieldWithPath("portfolioUrl").type(JsonFieldType.STRING).description("포트폴리오 URL")
                                        )
                                        .build()
                        )
                ));

        verify(userService).findEditInfo(any());
    }

    @DisplayName("회원정보 수정")
    @Test
    @WithCustomUser
    void update() throws Exception {
        mockMvc.perform(patch("/api/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andDo(document("user",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User")
                                        .summary("회원정보 수정")
                                        .description("해당 아이디의 유저의 회원정보를 수정한다")
                                        .pathParameters(
                                                parameterWithName("id").description("유저 아이디")
                                        )
                                        .requestHeaders(
                                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                        )
                                        .build()
                        )))
                .andDo(print());
    }

    @DisplayName("회원 삭제")
    @Test
    @WithCustomUser
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/user/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andDo(document("user",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User")
                                        .summary("회원정보 삭제")
                                        .description("해당 아이디의 유저를 삭제한다")
                                        .pathParameters(
                                                parameterWithName("id").description("유저 아이디")
                                        )
                                        .requestHeaders(
                                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                        )
                                        .build()
                        )));
    }

    @DisplayName("닉네임 중복여부 검사")
    @Test
    @WithCustomUser
    void duplicate() throws Exception {
        doReturn(true).when(userService).duplicateNickname(any());
        mockMvc.perform(get("/api/user/duple/zz")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("user/duple",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User")
                                        .summary("닉네임 중복여부 검사")
                                        .description("해당 닉네임의 중복여부를 검사한다")
                                        .pathParameters(
                                                parameterWithName("nickname").description("닉네임")
                                        )
                                        .build()
                        )));
        verify(userService).duplicateNickname(any());
    }

    @DisplayName("이미지 업로드")
    @Test
    @WithCustomUser
    void uploadImage() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.png", "image/png", "이미지".getBytes());
        mockMvc.perform(multipart("/api/user/image")
                        .file(multipartFile)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(document("user/image",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User")
                                        .summary("이미지 업로드")
                                        .description("이미지를 업로드한다")
                                        .requestHeaders(
                                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("file").type(JsonFieldType.OBJECT).description("이미지")
                                        )
                                        .build()
                        )));
    }

    @DisplayName("이미지 다운로드")
    @Test
    @WithCustomUser
    void downloadImage() throws Exception {

    }
}
