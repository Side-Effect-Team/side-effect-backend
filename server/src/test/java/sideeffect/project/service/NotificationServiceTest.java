package sideeffect.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sideeffect.project.domain.notification.Notification;
import sideeffect.project.domain.notification.NotificationType;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.notification.NotificationResponse;
import sideeffect.project.dto.notification.NotificationScrollResponse;
import sideeffect.project.repository.NotificationRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;

    @Mock
    NotificationRepository notificationRepository;

    User user;
    Notification notification;
    @BeforeEach
    void beforeEach(){
        user = User.builder()
                .id(1L)
                .email("google@google.com")
                //.password(encoder.encode("1234"))
                .nickname("ABC")
                .introduction("안녕하세요")
                .position(PositionType.BACKEND)
                .career("junior")
                .providerType(ProviderType.GOOGLE)
                .blogUrl("tistory/tlsrl6427")
                .githubUrl("github/tlsrl6427")
                .portfolioUrl("naver.com/tlsrl6427")
                .userRoleType(UserRoleType.ROLE_USER)
                .notifications(List.of(
                        new Notification(1L, "제목1", "내용1", "", false, user, user, NotificationType.APPROVE),
                        new Notification(2L, "제목2", "내용2", "", false, user, user, NotificationType.APPROVE),
                        new Notification(3L, "제목3", "내용3", "", true, user, user, NotificationType.APPROVE)
                ))
                .build();

        notification = Notification.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .link("/api/recruit-board/1")
                .watched(false)
                .user(user)
                .build();
    }

    @DisplayName("알림 목록 조회")
    @Test
    void view() {
        List<NotificationResponse> notificationResponses = notificationService.view(user);

        assertThat(notificationResponses).hasSize(3);
    }

    @Test
    void watch() {
        doReturn(Optional.of(notification)).when(notificationRepository).findById(any());

        notificationService.watch(user, 1L);

        assertAll(
                () -> verify(notificationRepository).findById(any()),
                () -> assertThat(notification.getWatched()).isEqualTo(true)
        );
    }

    @Test
    void delete() {
        doReturn(Optional.of(notification)).when(notificationRepository).findById(any());

        notificationService.delete(user, 1L);

        assertAll(
                () -> verify(notificationRepository).findById(any()),
                () -> verify(notificationRepository).deleteById(any())
        );
    }

    @Test
    void scroll() {
        doReturn(user.getNotifications()).when(notificationRepository).findByLastId(any(), any());

        NotificationScrollResponse notificationScrollResponse = notificationService.scroll(user, -1L);

        assertAll(
                () -> verify(notificationRepository).findByLastId(any(), any()),
                () -> assertThat(notificationScrollResponse.getNotificationResponses()).hasSize(3),
                () -> assertThat(notificationScrollResponse.getLastId()).isEqualTo(3L)
        );
    }

    @Test
    void getViewCount() {
        int viewCount = notificationService.getViewCount(user);

        assertThat(viewCount).isEqualTo(2);
    }
}