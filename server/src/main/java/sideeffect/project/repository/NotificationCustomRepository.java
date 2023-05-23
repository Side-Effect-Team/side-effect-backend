package sideeffect.project.repository;

import sideeffect.project.domain.notification.Notification;
import sideeffect.project.domain.user.User;

import java.util.List;

public interface NotificationCustomRepository {
    List<Notification> findByLastId(User user, Long lastId);
}
