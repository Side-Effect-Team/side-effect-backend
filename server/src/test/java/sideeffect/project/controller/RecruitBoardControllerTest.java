package sideeffect.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.ProgressType;
import sideeffect.project.domain.recruit.RecruitBoardType;
import sideeffect.project.domain.stack.StackLevelType;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.dto.recruit.BoardPositionResponse;
import sideeffect.project.dto.recruit.BoardStackResponse;
import sideeffect.project.dto.recruit.RecruitBoardResponse;
import sideeffect.project.service.RecruitBoardService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecruitBoardController.class)
class RecruitBoardControllerTest {

    @MockBean
    private RecruitBoardService recruitBoardService;

    private MockMvc mvc;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @DisplayName("모집게시글을 조회한다.")
    @Test
    void findRecruitBoard() throws Exception {
        RecruitBoardResponse response = RecruitBoardResponse.builder()
                .id(1L)
                .views(0)
                .title("모집 게시글 제목")
                .contents("모집 게시글 내용")
                .recruitBoardType(RecruitBoardType.PROJECT)
                .progressType(ProgressType.ONLINE)
                .deadline(LocalDateTime.now())
                .expectedPeriod("3개월")
                .positions(List.of(new BoardPositionResponse(PositionType.BACKEND, 3, 0)))
                .stacks(List.of(new BoardStackResponse(StackType.SPRING, StackLevelType.LOW, "url")))
                .build();

        given(recruitBoardService.findRecruitBoard(any())).willReturn(response);

        mvc.perform(get("/api/recruit-board/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", response.getTitle()).exists())
                .andExpect(jsonPath("$.contents", response.getContents()).exists())
                .andExpect(jsonPath("$.recruitBoardType", response.getRecruitBoardType()).exists())
                .andExpect(jsonPath("$.progressType", response.getProgressType()).exists())
                .andExpect(jsonPath("$.deadline", response.getDeadline()).exists())
                .andExpect(jsonPath("$.expectedPeriod", response.getExpectedPeriod()).exists())
                .andExpect(jsonPath("$.positions", response.getPositions()).exists())
                .andExpect(jsonPath("$.stacks", response.getStacks()).exists())
                .andDo(print());

        verify(recruitBoardService).findRecruitBoard(any());
    }

}