package sideeffect.project.repository;


import lombok.RequiredArgsConstructor;
import sideeffect.project.domain.notification.Notification;
import sideeffect.project.domain.user.User;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository{
    private final EntityManager em;

    @Override
    public List<Notification> findByLastId(User user, Long lastId) {
        if(lastId==null || lastId==-1){
            return em.createQuery("SELECT n FROM Notification n " +
                            "WHERE n.user = :user " +
                            "ORDER BY n.id desc", Notification.class)
                    .setParameter("user", user)
                    .setMaxResults(10).getResultList();
        }else{
            return em.createQuery( "SELECT n FROM Notification n " +
                            "WHERE n.user = :user " +
                            "AND n.id < :lastId " +
                            "ORDER BY n.id desc", Notification.class)
                    .setParameter("user", user)
                    .setParameter("lastId", lastId)
                    .setMaxResults(10).getResultList();
        }
    }
}
