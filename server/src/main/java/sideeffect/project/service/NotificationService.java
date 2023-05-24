package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.notification.Notification;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.notification.NotificationResponse;
import sideeffect.project.dto.notification.NotificationScrollResponse;
import sideeffect.project.repository.NotificationRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    public List<NotificationResponse> view(User user){
        List<NotificationResponse> notificationResponses = Collections.emptyList();
        List<Notification> notifications = user.getNotifications();
        if(notifications!=null && !notifications.isEmpty()){
            notificationResponses = notifications.stream()
                    .map(notification -> NotificationResponse.of(notification))
                    .collect(Collectors.toList());
        }

        return notificationResponses;
    }

    public String watch(User user, Long id){
        Notification findNotification = notificationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND));
        validateOwner(user, findNotification.getUser());
        findNotification.watched();
        return "watched success";
    }

    public String delete(User user, Long id){
        Notification findNotification = notificationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND));
        validateOwner(user, findNotification.getUser());
        notificationRepository.deleteById(findNotification.getId());
        return "delete success";
    }

    public NotificationScrollResponse scroll(User user, Long lastId){
        List<Notification> notifications = notificationRepository.findByLastId(user, lastId);
        return NotificationScrollResponse.of(NotificationResponse.listOf(notifications));
    }

    public int getViewCount(User user){
        int count = 0;
        List<Notification> notifications = user.getNotifications();
        for (Notification notification : notifications) {
            if(!notification.getWatched()) count++;
        }
        return count;
    }
    private void validateOwner(User user, User findUser) {
        if(user.getId() != findUser.getId()) throw new AuthException(ErrorCode.USER_UNAUTHORIZED);
    }
}
