package com.example.secondmate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.secondmate.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 알림 목록
    Page<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
 
    // 안 읽은 알림 
    boolean existsByUser_UserIdAndIsReadFalse(Long userId);

    // 안 읽은 알림 개수
    long countByUser_UserIdAndIsReadFalse(Long userId);
}
