package sideeffect.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sideeffect.project.controller.UserController;
import sideeffect.project.dto.user.TokenInfo;
import sideeffect.project.dto.user.UserJoinRequest;
import sideeffect.project.dto.user.UserLoginRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {


    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    UserJoinRequest userJoinRequest;
    @BeforeEach
    void beforeEach(WebApplicationContext context){
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();

        userJoinRequest = UserJoinRequest.builder()
                .email("1111@gmail.com")
                .password("1234")
                .nickname("ABC")
                .build();
    }
    @Test
    @DisplayName("회원가입")
    void join() throws Exception {

        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}