package sideeffect.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
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
    void beforeEach(WebApplicationContext context){
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
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
        mockMvc.perform(get("/api/user/mypage/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value(userResponse.getEmail()))
                .andExpect(jsonPath("nickname").value(userResponse.getNickname()))
                .andExpect(jsonPath("blogUrl").value(userResponse.getBlogUrl()))
                .andExpect(jsonPath("githubUrl").value(userResponse.getGithubUrl()))
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
                .andDo(print());

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
                .andDo(print());
    }

    @DisplayName("회원 삭제")
    @Test
    @WithCustomUser
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/user/1")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @DisplayName("닉네임 중복여부 검사")
    @Test
    @WithCustomUser
    void duplicate() throws Exception {
        doReturn(true).when(userService).duplicateNickname(any());
        mockMvc.perform(get("/api/user/duple/zz")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
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
                .andExpect(status().isOk());
    }

    @DisplayName("이미지 다운로드")
    @Test
    @WithCustomUser
    void downloadImage() throws Exception {

    }
}
