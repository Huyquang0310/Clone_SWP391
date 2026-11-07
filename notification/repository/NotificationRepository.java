package fu.libmanagement.notification.repository;

import fu.libmanagement.common.enums.NotificationType;
import fu.libmanagement.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndSeenFalse(Long userId);

    /** Dùng để không gửi trùng một nhắc nhở (cùng người, loại, phiếu và nội dung). */
    boolean existsByUserIdAndTypeAndRefIdAndContent(Long userId, NotificationType type, Long refId, String content);

    @Modifying
    @Query("update Notification n set n.seen = true where n.userId = :userId and n.seen = false")
    int markAllSeen(@Param("userId") Long userId);
}
