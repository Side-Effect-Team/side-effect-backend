package sideeffect.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import sideeffect.project.common.security.WithCustomUser;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.notification.NotificationResponse;
import sideeffect.project.dto.notification.NotificationScrollResponse;
import sideeffect.project.service.NotificationService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private MockMvc mockMvc;

    private User user;
    @BeforeEach
    void beforeEach(){
        user = User.builder()
                .id(1L)
                .email("1111@naver.com")
                .password("1234")
                .nickname("ABC")
                .userRoleType(UserRoleType.ROLE_USER)
                .build();
    }

    @DisplayName("알림 목록 조회")
    @Test
    @WithCustomUser
    void view() throws Exception {
        List<NotificationResponse> notifications = createNotificationResponses(3);
        doReturn(notifications).when(notificationService).view(any());

        mockMvc.perform(get("/api/notice")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3));

        verify(notificationService).view(any());
    }

    @DisplayName("알림 읽음 표시")
    @Test
    @WithCustomUser
    void watch() throws Exception {
        mockMvc.perform(post("/api/notice/1")
                .with(csrf()))
                .andExpect(status().isOk());

    }

    @DisplayName("알림 삭제")
    @Test
    @WithCustomUser
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/notice/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @DisplayName("스크롤")
    @Test
    @WithCustomUser
    void scroll() throws Exception {
        NotificationScrollResponse notificationScrollResponse = NotificationScrollResponse.of(createNotificationResponses(3));
        doReturn(notificationScrollResponse).when(notificationService).scroll(any(), any());

        mockMvc.perform(get("/api/notice/scroll/1")
                .with(csrf()))
                .andExpect(jsonPath("$.notificationResponses.length()").value(notificationScrollResponse.getNotificationResponses().size()))
                .andExpect(jsonPath("$.lastId").value(notificationScrollResponse.getLastId()))
                .andExpect(status().isOk());

        verify(notificationService).scroll(any(), any());
    }

    @DisplayName("안읽은 알림 개수 조회")
    @Test
    @WithCustomUser
    void viewCount() throws Exception {
        doReturn(3).when(notificationService).getViewCount(any());

        mockMvc.perform(get("/api/notice/view-count")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        verify(notificationService).getViewCount(any());
    }

    private List<NotificationResponse> createNotificationResponses(int n) {
        List<NotificationResponse> notificationResponses = new ArrayList<>();
        for(int i=1; i<=n; i++){
            notificationResponses.add(NotificationResponse.builder()
                    .id((long) i)
                    .title("제목"+i)
                    .contents("내용"+i)
                    .build());
        }

        return notificationResponses;
    }
}
