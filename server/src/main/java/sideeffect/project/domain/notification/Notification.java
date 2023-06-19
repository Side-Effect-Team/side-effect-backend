package sideeffect.project.domain.notification;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sideeffect.project.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
@EntityListeners(value = AuditingEntityListener.class)
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String contents;
    private String link;

    private Boolean watched;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private NotificationType notificationType;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sending_user_id")
    private User sendingUser;

    @Builder
    public Notification(Long id, String title, String contents, String link, Boolean watched, User user, User sendingUser, NotificationType notificationType) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.link = link;
        this.watched = watched;
        this.user = user;
        this.sendingUser = sendingUser;
        this.notificationType = notificationType;
    }

    public void watched(){
        this.watched = true;
    }
}
