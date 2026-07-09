package com.example.secondmate.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.secondmate.common.NotificationType;
import com.example.secondmate.common.ProcessReason;
import com.example.secondmate.common.ReportStatus;
import com.example.secondmate.dto.NotificationDTO;
import com.example.secondmate.entity.Comment;
import com.example.secondmate.entity.Inquiry;
import com.example.secondmate.entity.Notification;
import com.example.secondmate.entity.Product;
import com.example.secondmate.entity.Report;
import com.example.secondmate.entity.User;
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
                        + "\n\n자세한 내용은 마이페이지에서 확인해주세요.")
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
                        + "\n\n자세한 내용은 마이페이지에서 확인해주세요.")
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
                        + "\n\n자세한 내용은 마이페이지에서 확인해주세요.")
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
                        + "\n\n자세한 내용은 마이페이지에서 확인해주세요.")
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
                        + "\n\n자세한 내용은 마이페이지에서 확인해주세요.")
                .noteType(reportStatus == ReportStatus.ACCEPTED
                        ? NotificationType.REPORT_ACCEPTED
                        : NotificationType.REPORT_REJECTED)
                .build();

        notificationRepository.save(notification);
    }

    // 계정 정지 회원 알림 생성
    public void createUserSuspendedNotification(User user) {
        Notification notification = Notification.builder()
                                                .user(user)
                                                .product(null)
                                                .productTitle("계정 이용 제한")
                                                .title("[계정] 이용 정지 처리")
                                                .content("신고 누적으로 인해 계정 이용이 제한되었습니다."
                                                         + "\n정지 해제 예정일 : " + user.getSuspendedUntil()
                                                         + "\n\n자세한 내용은 마이페이지에서 확인해주세요.")
                                                .noteType(NotificationType.USER_SUSPENDED)
                                                .build();

        notificationRepository.save(notification);
    }

    // 계정 정지 해제 알림 생성
    public void createUserUnsuspendedNotification(User user) {
        Notification notification = Notification.builder()
                                                .user(user)
                                                .product(null)
                                                .productTitle("계정 이용 제한 해제")
                                                .title("[계정] 이용 정지 해제")
                                                .content("계정 이용 정지가 해제되었습니다."
                                                         + "\n\n자세한 내용은 마이페이지에서 확인해주세요.")
                                                .noteType(NotificationType.USER_UNSUSPENDED)
                                                .build();

        notificationRepository.save(notification);
    }

    // 문의 답변 알림 생성
    public void createInquiryAnsweredNotification(Inquiry inquiry) {
        Notification notification = Notification.builder()
                                                .user(inquiry.getUser())
                                                .product(null)
                                                .productTitle("문의 답변")
                                                .title("[문의] 답변 등록")
                                                .content("문의하신 '" + inquiry.getTitle() + "'에 답변이 등록되었습니다."
                                                         + "\n\n자세한 내용은 마이페이지에서 확인해주세요.")
                                                .noteType(NotificationType.INQUIRY_ANSWERED)
                                                .build();
                                        
        notificationRepository.save(notification);
    }

    // 쪽지 목록 조회
    public Page<NotificationDTO> getNotificationList(Long userId, Pageable pageable) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toDTO);
    }

    // 알림 읽음 처리
    public void readNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림"));

        if (!notification.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("본인 알림만 열람 가능");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // 안 읽은 알림
    public boolean hasUnreadNotification(Long userId) {
        return notificationRepository.existsByUser_UserIdAndIsReadFalse(userId);
    }

    // 안 읽은 알림 개수
    public long getMyUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
    }

    // 쪽지 삭제
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                                                          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림"));

        if (!notification.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("본인 알림만 삭제 가능");
        }

        notificationRepository.delete(notification);
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
