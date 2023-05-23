package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustomRepository {

}
