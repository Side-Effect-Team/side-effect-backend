package sideeffect.project.dto.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import sideeffect.project.domain.notification.Notification;
import sideeffect.project.domain.notification.NotificationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationResponse {

    private Long id;
    private String title;
    private String contents;
    private String link;
    private Boolean watched;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private Long userId;

    public static NotificationResponse of(Notification notification){
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .contents(notification.getNotificationType()==NotificationType.REGISTER || notification.getNotificationType()==NotificationType.COMMENT ?
                        notification.getSendingUser().getNickname() + notification.getContents() : notification.getContents())
                .link(notification.getLink())
                .watched(notification.getWatched())
                .createdAt(notification.getCreatedAt())
                .userId(notification.getSendingUser().getId())
                .build();
    }

    public static List<NotificationResponse> listOf(List<Notification> notifications){
        return notifications.stream()
                .map(notification -> of(notification))
                .collect(Collectors.toList());
    }
}
