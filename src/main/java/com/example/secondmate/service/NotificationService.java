package com.example.secondmate.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.secondmate.common.NotificationType;
import com.example.secondmate.common.ProcessReason;
import com.example.secondmate.common.ReportStatus;
import com.example.secondmate.dto.NotificationDTO;
import com.example.secondmate.entity.Comment;
import com.example.secondmate.entity.Notification;
import com.example.secondmate.entity.Product;
import com.example.secondmate.entity.Report;
import com.example.secondmate.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    // 상품 숨김 쪽지 생성
    public void createProductHiddenNotification(Product product, ProcessReason processReason, String detailReason) {
        Notification notification = Notification.builder()
                .user(product.getUser())
                .product(product)
                .productTitle(product.getTitle())
                .title("[게시글] 숨김 처리")
                .content("처리 사유 : " + processReason.getReasonName()
                        + "\n상세 사유 : " + detailReason
                        + "\n\n자세한 내용은 고객센터에 문의해주세요.")
                .noteType(NotificationType.PRODUCT_HIDDEN)
                .build();

        notificationRepository.save(notification);
    }

    // 상품 삭제 쪽지 생성
    public void createProductDeletedNotification(Product product, ProcessReason processReason, String detailReason) {
        Notification notification = Notification.builder()
                .user(product.getUser())
                .product(null)
                .productTitle(product.getTitle())
                .title("[게시글] 삭제 처리")
                .content("처리 사유 : " + processReason.getReasonName()
                        + "\n상세 사유 : " + detailReason
                        + "\n\n자세한 내용은 고객센터에 문의해주세요.")
                .noteType(NotificationType.PRODUCT_DELETED)
                .build();

        notificationRepository.save(notification);
    }

    // 댓글 숨김 쪽지 생성
    public void createCommentHiddenNotification(Comment comment, ProcessReason processReason, String detailReason) {
        Notification notification = Notification.builder()
                .user(comment.getUser())
                .product(comment.getProduct())
                .productTitle(comment.getProduct().getTitle())
                .title("[댓글] 숨김 처리")
                .content("처리 사유 : " + processReason.getReasonName()
                        + "\n상세 사유 : " + detailReason
                        + "\n\n자세한 내용은 고객센터에 문의해주세요.")
                .noteType(NotificationType.COMMENT_HIDDEN)
                .build();

        notificationRepository.save(notification);
    }

    // 댓글 삭제 쪽지 생성
    public void createCommentDeletedNotification(Comment comment, ProcessReason processReason, String detailReason) {
        Notification notification = Notification.builder()
                .user(comment.getUser())
                .product(null)
                .productTitle(comment.getProduct().getTitle())
                .title("[댓글] 삭제 처리")
                .content("처리 사유 : " + processReason.getReasonName()
                        + "\n상세 사유 : " + detailReason
                        + "\n\n자세한 내용은 고객센터에 문의해주세요.")
                .noteType(NotificationType.COMMENT_DELETED)
                .build();

        notificationRepository.save(notification);
    }

    // 신고 처리 쪽지 생성
    public void createReportProcessedNotification(Report report, ReportStatus reportStatus) {
        String resultText = reportStatus == ReportStatus.ACCEPTED ? "수락" : "반려";

        Notification notification = Notification.builder()
                .user(report.getReporter())
                .product(null)
                .productTitle("신고 처리")
                .title("[신고] 처리 완료")
                .content("신고하신 건이 " + resultText + " 처리되었습니다."
                        + "\n\n자세한 내용은 고객센터에 문의해주세요.")
                .noteType(reportStatus == ReportStatus.ACCEPTED
                        ? NotificationType.REPORT_ACCEPTED
                        : NotificationType.REPORT_REJECTED)
                .build();

        notificationRepository.save(notification);
    }

    // 쪽지 목록 조회
    public Page<NotificationDTO> getNotificationList(Long userId, Pageable pageable) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toDTO);
    }

    // 알림 읽음 처리
    public void readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // Entity -> DTO 변환
    private NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUser().getUserId())
                .productId(notification.getProduct() != null ? notification.getProduct().getProductId() : null)
                .productTitle(notification.getProductTitle())
                .title(notification.getTitle())
                .content(notification.getContent())
                .noteType(notification.getNoteType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
