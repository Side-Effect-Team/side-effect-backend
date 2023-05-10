package sideeffect.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.security.WithCustomUser;
import sideeffect.project.config.WebSecurityConfig;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.recruit.*;
import sideeffect.project.security.UserDetailsServiceImpl;
import sideeffect.project.service.RecruitBoardService;

import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(WebSecurityConfig.class)
@ComponentScan(basePackages = "sideeffect.project.security")
@WebMvcTest(RecruitBoardController.class)
class RecruitBoardControllerTest {

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private RecruitBoardService recruitBoardService;

    private MockMvc mvc;
    private User user;
    private RecruitBoard recruitBoard;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        user = User.builder()
                .id(1L)
                .email("test@naver.com")
                .password("qwer1234!")
                .build();

        objectMapper = new ObjectMapper();
    }

    @DisplayName("모집게시글을 조회한다.")
    @Test
    void findRecruitBoard() throws Exception {
        RecruitBoardResponse response = RecruitBoardResponse.builder()
                .id(1L)
                .views(0)
                .title("모집 게시글 제목")
                .projectName("프로젝트명1")
                .content("모집 게시글 내용")
                .positions(List.of(new BoardPositionResponse(1L, PositionType.BACKEND.getValue(), 3, 0)))
                .tags(List.of(new BoardStackResponse(StackType.SPRING.getValue(), "url")))
                .build();

        given(recruitBoardService.findRecruitBoard(any())).willReturn(response);

        mvc.perform(get("/api/recruit-board/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andExpect(jsonPath("$.projectName").value(response.getProjectName()))
                .andExpect(jsonPath("$.content").value(response.getContent()))
                .andExpect(jsonPath("$.positions.length()").value(1))
                .andExpect(jsonPath("$.tags.length()").value(1))
                .andDo(print());

        verify(recruitBoardService).findRecruitBoard(any());
    }

    @DisplayName("모집게시글 목록을 조회한다.")
    @Test
    void findScrollRecruitBoard() throws Exception {
        RecruitBoard recruitBoard1 = RecruitBoard.builder().id(10L).title("모집 게시판1").contents("모집합니다1.").build();
        RecruitBoard recruitBoard2 = RecruitBoard.builder().id(5L).title("모집 게시판2").contents("모집합니다2.").build();
        recruitBoard1.associateUser(user);
        recruitBoard2.associateUser(user);
        List<RecruitBoardResponse> recruitBoardResponses = RecruitBoardResponse.listOf(List.of(recruitBoard1, recruitBoard2));
        RecruitBoardScrollResponse scrollResponse = RecruitBoardScrollResponse.of(recruitBoardResponses, false);

        given(recruitBoardService.findRecruitBoards(any())).willReturn(scrollResponse);

        mvc.perform(get("/api/recruit-board/scroll")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", "11")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruitBoards.length()").value(2))
                .andExpect(jsonPath("$.lastId").value(5L))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andDo(print());

        verify(recruitBoardService).findRecruitBoards(any());
    }

    @DisplayName("모집 게시판을 등록한다.")
    @WithCustomUser
    @Test
    void registerRecruitBoard() throws Exception {
        RecruitBoardRequest request = RecruitBoardRequest.builder().title("모집 게시판1").content("모집합니다1.").build();
        RecruitBoard recruitBoard = RecruitBoard.builder().id(10L).title("모집 게시판1").contents("모집합니다1.").build();
        RecruitBoardResponse response = RecruitBoardResponse.of(recruitBoard);

        given(recruitBoardService.register(any(), any())).willReturn(response);
        mvc.perform(post("/api/recruit-board")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("모집 게시판을 업데이트한다.")
    @WithCustomUser
    @Test
    void updateBoard() throws Exception {
        RecruitBoardRequest request = RecruitBoardRequest.builder().title("모집 게시판1").content("모집합니다1.").build();

        mvc.perform(patch("/api/recruit-board/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @DisplayName("잘못된 유저가 모집 게시판을 업데이트 한다.")
    @WithCustomUser
    @Test
    void updateBoardByNotOwner() throws Exception {
        RecruitBoardRequest request = RecruitBoardRequest.builder().title("모집 게시판1").content("모집합니다1.").build();
        doThrow(new AuthException(ErrorCode.RECRUIT_BOARD_UNAUTHORIZED))
                .when(recruitBoardService).updateRecruitBoard(any(), any(), any());
        mvc.perform(patch("/api/recruit-board/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @DisplayName("모집 게시판을 삭제한다.")
    @WithCustomUser
    @Test
    void deleteBoard() throws Exception {
        mvc.perform(delete("/api/recruit-board/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @DisplayName("모집 게시판 글 작성자가 아니라면 삭제가 불가능하다.")
    @WithCustomUser
    @Test
    void deleteBoardByNotOwner() throws Exception {
        doThrow(new AuthException(ErrorCode.RECRUIT_BOARD_UNAUTHORIZED))
                .when(recruitBoardService).deleteRecruitBoard(any(), any());
        mvc.perform(delete("/api/recruit-board/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

}