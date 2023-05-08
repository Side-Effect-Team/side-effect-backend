package sideeffect.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sideeffect.project.common.security.WithCustomUser;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.user.UserRequest;
import sideeffect.project.dto.user.UserResponse;
import sideeffect.project.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    MockMvc mockMvc;

    @MockBean
    UserService userService;

    ObjectMapper objectMapper;

    UserRequest userJoinRequest;

    UserResponse userResponse;

    @BeforeEach
    void beforeEach(WebApplicationContext context){
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userJoinRequest = UserRequest.builder()
                .email("1111@gmail.com")
                .password("1234")
                .nickname("ABC")
                .build();

        userResponse = UserResponse.builder()
                .email("1111@gmail.com")
                .nickname("ABC")
                .blogUrl("tistory/1111.com")
                .githubUrl("github/1111.com")
                .userRoleType(UserRoleType.ROLE_USER)
                .build();
    }
    @Test
    @DisplayName("회원가입")
    void join() throws Exception {

        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("단건조회")
    @WithCustomUser
    void view() throws Exception {
        when(userService.findOne(any(), any())).thenReturn(userResponse);
        mockMvc.perform(get("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email", userResponse.getEmail()).exists())
                .andExpect(jsonPath("nickname", userResponse.getNickname()).exists())
                .andExpect(jsonPath("blogUrl", userResponse.getBlogUrl()).exists())
                .andExpect(jsonPath("githubUrl", userResponse.getGithubUrl()).exists())
                .andExpect(jsonPath("userRoleType", userResponse.getUserRoleType()).exists())
                .andDo(print());
        verify(userService).findOne(any(), any());
    }
}