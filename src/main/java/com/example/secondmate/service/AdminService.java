package com.example.secondmate.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondmate.common.ProcessReason;
import com.example.secondmate.common.ProductCategory;
import com.example.secondmate.common.ReportStatus;
import com.example.secondmate.common.TradeStatus;
import com.example.secondmate.common.UserStatus;
import com.example.secondmate.entity.Comment;
import com.example.secondmate.entity.Product;
import com.example.secondmate.entity.User;
import com.example.secondmate.repository.CommentRepository;
import com.example.secondmate.repository.ProductRepository;
import com.example.secondmate.repository.ReportRepository;
import com.example.secondmate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final NotificationService notificationService;

    // 회원 검색
    public Page<User> getUserList(UserStatus status, String keyword, Pageable pageable) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        return userRepository.searchUsers(status, keyword, pageable);
    }

    // 상품 검색
    public Page<Product> getProductList(TradeStatus tradeStatus, List<ProductCategory> categories, String keyword,
            Pageable pageable) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        if (categories != null && categories.isEmpty()) {
            categories = null;
        }

        return productRepository.searchAdminProducts(tradeStatus, categories, keyword, pageable);
    }

    // 관리자 상품 숨김 처리
    @Transactional
    public void hideProduct(Long productId, ProcessReason processReason, String hiddenReason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

        product.setHidden(true);
        product.setHiddenReason(hiddenReason);

        notificationService.createProductHiddenNotification(product, processReason, hiddenReason);
    }

    // 관리자 상품 숨김 해제
    @Transactional
    public void showProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

        product.setHidden(false);
        product.setHiddenReason(null);
    }

    // 관리자 상품 강제 삭제
    @Transactional
    public void deleteProduct(Long productId, ProcessReason processReason, String deleteReason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

        notificationService.createProductDeletedNotification(product, processReason, deleteReason);

        commentRepository.deleteByProduct_ProductId(productId);
        productRepository.deleteById(productId);
    }

    // 댓글 숨김
    @Transactional
    public void hideComment(Long commentId, ProcessReason processReason, String detailReason) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글"));

        comment.setHidden(true);
        comment.setHiddenReason(detailReason);

        notificationService.createCommentHiddenNotification(comment, processReason, detailReason);
    }

    // 댓글 숨김 해제
    @Transactional
    public void showComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글"));

        comment.setHidden(false);
        comment.setHiddenReason(null);
    }

    // 댓글 강제 삭제
    @Transactional
    public void deleteComment(Long commentId, ProcessReason processReason, String detailReason) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글"));

        notificationService.createCommentDeletedNotification(comment, processReason, detailReason);
        Product product = comment.getProduct();
        commentRepository.delete(comment);
        product.setCommentCount(product.getCommentCount() - 1);
    }

    // 전체 회원 수
    public long getTotalUserCount() {
        return userRepository.count();
    }

    // 정지 회원 수
    public long getSuspendedUserCount() {
        return userRepository.countByStatus(UserStatus.SUSPENDED);
    }

    // 오늘 등록된 상품 수
    public long getTodayProductCount() {
        LocalDateTime startDate = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(1);

        return productRepository.countByRegDateBetween(startDate, endDate);
    }

    // 최근 등록된 상품 5개
    public Page<Product> getRecentProducts() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "regDate"));

        return productRepository.findAll(pageable);
    }

    // 전체 신고 수
    public long getTotalReportCount() {
        return reportRepository.count();
    }

    // 처리 대기 신고 수
    public long getPendingReportCount() {
        return reportRepository.countByReportStatus(ReportStatus.PENDING);
    }

    // 처리 완료 신고 수
    public long getAcceptedReportCount() {
        return reportRepository.countByReportStatus(ReportStatus.ACCEPTED);
    }
}
