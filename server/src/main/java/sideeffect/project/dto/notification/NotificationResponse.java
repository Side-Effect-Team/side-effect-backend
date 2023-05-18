package sideeffect.project.dto.notification;

import lombok.*;
import sideeffect.project.domain.notification.Notification;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;

    public static NotificationResponse of(Notification notification){
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .contents(notification.getContents())
                .link(notification.getLink())
                .watched(notification.getWatched())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}