package com.example.secondmate.dto;

import java.time.LocalDateTime;

import com.example.secondmate.common.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long notificationId;
    private Long userId;
    private Long productId;
    private String productTitle;
    private String title;
    private String content;
    private NotificationType noteType;
    private boolean isRead;
    private LocalDateTime createdAt;
}
