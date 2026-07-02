package com.example.secondmate.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondmate.common.ReportStatus;
import com.example.secondmate.common.ReportType;
import com.example.secondmate.common.TargetType;
import com.example.secondmate.common.UserStatus;
import com.example.secondmate.entity.Comment;
import com.example.secondmate.entity.Product;
import com.example.secondmate.entity.Report;
import com.example.secondmate.entity.User;
import com.example.secondmate.repository.CommentRepository;
import com.example.secondmate.repository.ProductRepository;
import com.example.secondmate.repository.ReportRepository;
import com.example.secondmate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    // 상품 신고
    @Transactional
    public void reportProduct(Long userId, Long productId, ReportType reportType, String reason) {
        // 이미 신고했는지 확인
        if (existsReport(userId, TargetType.PRODUCT, productId)) {
            throw new IllegalArgumentException("이미 신고한 상품");
        }

        // 신고자 확인
        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        // 신고 대상 상품 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

        Report report = Report.builder()
                .reporter(reporter)
                .reportedUser(product.getUser())
                .targetType(TargetType.PRODUCT)
                .targetId(product.getProductId())
                .reason(reason)
                .reportStatus(ReportStatus.PENDING)
                .reportType(reportType)
                .build();

        reportRepository.save(report);
    }

    // 댓글 신고
    @Transactional
    public void reportComment(Long userId, Long commentId, ReportType reportType, String reason) {
        // 이미 신고했는지 확인
        if (existsReport(userId, TargetType.COMMENT, commentId)) {
            throw new IllegalArgumentException("이미 신고한 댓글");
        }

        // 신고자 확인
        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        // 신고 대상 댓글 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글"));

        Report report = Report.builder()
                .reporter(reporter)
                .reportedUser(comment.getUser())
                .targetType(TargetType.COMMENT)
                .targetId(comment.getCommentId())
                .reason(reason)
                .reportStatus(ReportStatus.PENDING)
                .reportType(reportType)
                .build();

        reportRepository.save(report);
    }

    // 사용자 신고
    @Transactional
    public void reportUser(Long userId, Long reportedUserId, ReportType reportType, String reason) {
        if (existsReport(userId, TargetType.USER, reportedUserId)) {
            throw new IllegalArgumentException("이미 신고한 사용자");
        }

        // 본인이 본인을 신고하는 경우
        if (userId.equals(reportedUserId)) {
            throw new IllegalArgumentException("본인을 신고할 수 없음");
        }

        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        User reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        Report report = Report.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .targetType(TargetType.USER)
                .targetId(reportedUser.getUserId())
                .reason(reason)
                .reportStatus(ReportStatus.PENDING)
                .reportType(reportType)
                .build();

        reportRepository.save(report);
    }

    // 중복 신고 확인
    public boolean existsReport(Long userId, TargetType targetType, Long targetId) {
        return reportRepository.existsByReporter_UserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }

    // 신고 목록 조회
    public List<Report> getReportList(ReportStatus reportStatus) {
        return reportRepository.findByReportStatus(reportStatus);
    }

    // 신고 처리 상태 변경
    @Transactional
    public void changeReportStatus(Long reportId, ReportStatus reportStatus) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고"));

        // 이미 처리된 신고를 다시 하지 못하게
        if (report.getReportStatus() != ReportStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 신고");
        }

        report.setReportStatus(reportStatus);
        notificationService.createReportProcessedNotification(report, reportStatus);

        // 관리자가 신고를 처리한 경우에만 제재 처리
        if (reportStatus == ReportStatus.ACCEPTED) {
            // 댓글 신고 : 댓글 숨김 처리
            if (report.getTargetType() == TargetType.COMMENT) {
                Comment comment = commentRepository.findById(report.getTargetId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글"));

                comment.setHidden(true);
            }

            // 상품 신고 : 수락된 신고 3건 이상일 경우
            if (report.getTargetType() == TargetType.PRODUCT) {
                long productReportCount = reportRepository.countByTargetTypeAndTargetIdAndReportStatus(
                        TargetType.PRODUCT, report.getTargetId(), ReportStatus.ACCEPTED);

                if (productReportCount >= 3) {
                    Product product = productRepository.findById(report.getTargetId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

                    product.setHidden(true);
                }
            }

            // 신고 수락 누적 3건 이상이면 계정 정지
            long userReportCount = reportRepository.countByReportedUser_UserIdAndReportStatus(
                    report.getReportedUser().getUserId(), ReportStatus.ACCEPTED);

            if (userReportCount >= 3 && report.getReportedUser().getStatus() != UserStatus.SUSPENDED) {
                suspendUser(report.getReportedUser());
            }
        }
    }

    // 정지 횟수에 따른 정지 기간 적용
    private void suspendUser(User user) {
        int suspensionCount = user.getSuspensionCount() + 1;

        user.setSuspensionCount(suspensionCount);
        user.setStatus(UserStatus.SUSPENDED);

        if (suspensionCount == 1) {
            user.setSuspendedUntil(LocalDateTime.now().plusDays(3));
        } else if (suspensionCount == 2) {
            user.setSuspendedUntil(LocalDateTime.now().plusDays(7));
        } else if (suspensionCount == 3) {
            user.setSuspendedUntil(LocalDateTime.now().plusDays(30));
        } else {
            user.setSuspendedUntil(LocalDateTime.now().plusYears(100));
        }

        notificationService.createUserSuspendedNotification(user);
    }

    // 로그인한 사용자가 받은 수락된 신고 목록 조회
    public List<Report> getAcceptedReports(Long userId) {
        return reportRepository.findByReportedUser_UserIdAndReportStatus(userId, ReportStatus.ACCEPTED);
    }

    // 로그인한 사용자가 받은 수락된 신고 횟수
    public long getReportCount(Long userId, ReportStatus reportStatus) {
        return reportRepository.countByReportedUser_UserIdAndReportStatus(userId, reportStatus);
    }

    // 로그인 모달에 표시할 미확인 신고 수락 건수
    public long getUncheckedAcceptedReportCount(Long userId) {
        return reportRepository.countByReportedUser_UserIdAndReportStatusAndReportModalChecked(userId, ReportStatus.ACCEPTED, false);
    }

    // 신고 처리 모달 확인 처리
    @Transactional
    public void checkAcceptedReportModal(Long userId) {
        reportRepository.checkAcceptedReportModal(userId, ReportStatus.ACCEPTED);
    }

    // 관리자 신고 목록 조회
    public Page<Report> getAdminReportList(ReportStatus reportStatus, TargetType targetType,
            String keyword, Pageable pageable) {

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        return reportRepository.searchAdminReports(reportStatus, targetType, keyword, pageable);
    }

    // 신고 상세내역
    public Report getReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고"));
    }

    // 사용자가 신고한 내역 페이징 조회
    public Page<Report> getMyReportList(Long userId, Pageable pageable) {
        return reportRepository.findByReporter_UserId(userId, pageable);
    }

    // 사용자가 신고받은 내역 페이징 조회
    public Page<Report> getMyReceivedReportList(Long userId, Pageable pageable) {
        return reportRepository.findByReportedUser_UserId(userId, pageable);
    }
}
