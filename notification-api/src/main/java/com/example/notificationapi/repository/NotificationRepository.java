package com.example.notificationapi.repository;

import com.example.notificationapi.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
        import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n " +
            "WHERE n.status = 'QUEUED' AND (n.nextAttemptAt IS NULL OR n.nextAttemptAt <= :now) " +
            "ORDER BY n.createdAt")
    List<Notification> findNextQueued(@Param("now") LocalDateTime now, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.status = 'PROCESSING', n.processingStartedAt = :now " +
            "WHERE n.id = :id AND n.status = 'QUEUED'")
    int claimForProcessing(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.status = 'QUEUED', n.processingStartedAt = NULL " +
            "WHERE n.status = 'PROCESSING' AND n.processingStartedAt < :cutoff")
    int resetProcessingOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
