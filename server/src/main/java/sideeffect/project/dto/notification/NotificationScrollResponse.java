package sideeffect.project.dto.notification;

import lombok.*;

import java.util.List;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationScrollResponse {

    private List<NotificationResponse> notificationResponses;
    private long lastId;

    public static NotificationScrollResponse of(List<NotificationResponse> notificationResponses){
        if(notificationResponses.isEmpty()){
            return NotificationScrollResponse.builder()
                    .notificationResponses(notificationResponses)
                    .build();
        }

        return NotificationScrollResponse.builder()
                .notificationResponses(notificationResponses)
                .lastId(notificationResponses.get(notificationResponses.size()-1).getId())
                .build();
    }
}
