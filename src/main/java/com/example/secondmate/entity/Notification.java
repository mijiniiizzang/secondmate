package com.example.secondmate.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.secondmate.common.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long notificationId;

    // 알림 받는 회원
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    // 관련 상품
    // 상품 삭제 후에도 알림을 남겨야함
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    // 상품 삭제 후 어떤 상품 관련 알림인지 보여주기 위한 제목 저장
    @Column(nullable=false)
    private String productTitle;

    @Column(nullable=false)
    private String title;
    @Column(nullable=false, length=1000)
    private String content;

    // 알림 종류
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private NotificationType noteType;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="report_id")
    private Report report;

    @Builder.Default
    @Column(nullable=false)
    private boolean isRead = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
