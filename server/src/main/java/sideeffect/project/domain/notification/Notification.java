package sideeffect.project.domain.notification;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import sideeffect.project.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String contents;
    private String link;

    private Boolean watched;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Notification(Long id, String title, String contents, String link, Boolean watched, User user) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.link = link;
        this.watched = watched;
        this.user = user;
    }

    public void isWatched(Boolean flag){
        this.watched = flag;
    }
}
