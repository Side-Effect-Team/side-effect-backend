package sideeffect.project.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sideeffect.project.common.jpa.TestDataRepository;
import sideeffect.project.domain.notification.Notification;
import sideeffect.project.domain.user.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


class NotificationRepositoryTest extends TestDataRepository {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    User user;
    @BeforeEach
    void beforeEach(){
        user = User.builder()
                .email("test@gmail.com")
                .build();
        userRepository.save(user);
    }

    @DisplayName("lastId로 Notification List받기")
    @Test
    void findByLastId(){

        List<Notification> notifications = createNotifications(user, 5);
        Long lastId = notificationRepository.saveAll(notifications).get(4).getId();
        List<Notification> findNotifications = notificationRepository.findByLastId(user, lastId+1);

        assertAll(
                () -> assertThat(findNotifications).hasSize(5),
                () -> assertThat(findNotifications.get(findNotifications.size()-1).getId()).isEqualTo(lastId-4)
        );
    }

    @DisplayName("최초로 Notification List받기")
    @Test
    void findByLastIdInit(){

        List<Notification> notifications = createNotifications(user, 5);
        notificationRepository.saveAll(notifications);
        //List<Notification> findNotifications = notificationRepository.findByLastId(user, -1L);
        List<Notification> findNotifications = notificationRepository.findByLastId(user, null);

        assertAll(
                () -> assertThat(findNotifications).hasSize(5),
                () -> assertThat(findNotifications.get(findNotifications.size()-1).getId()).isEqualTo(1L)
        );
    }

    private List<Notification> createNotifications(User user, int n) {
        List<Notification> notifications = new ArrayList<>();

        for(int i=0; i<n; i++){
            notifications.add(Notification.builder()
                            .user(user)
                            .title("제목"+i)
                            .contents("내용"+i)
                            .watched(false)
                    .build());
        }

        return notifications;
    }

}