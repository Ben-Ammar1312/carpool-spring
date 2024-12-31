package gle.carpoolspring.repository;

import gle.carpoolspring.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUserId(Long userId) ;

    List<Notification> findByUserIdOrderByTimestampDesc(int userId);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.isRead = false ORDER BY n.timestamp DESC")
    List<Notification> findUnreadByUserIdOrderByTimestampDesc(@Param("userId") int userId);
}
