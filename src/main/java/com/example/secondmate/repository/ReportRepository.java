package com.example.secondmate.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.secondmate.common.TargetType;
import com.example.secondmate.entity.Report;
import com.example.secondmate.common.ReportStatus;


public interface ReportRepository extends JpaRepository<Report, Long> {
    // 신고 이력이 있는지?
    boolean existsByReporter_UserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    // 신고 상태 조회
    List<Report> findByReportStatus(ReportStatus reportStatus);

    // 특정 상품/댓글이 신고 수락된 횟수 조회
    long countByTargetTypeAndTargetIdAndReportStatus(
        TargetType targetType,
        Long targetId,
        ReportStatus reportStatus
    );

    // 특정 사용자의 신고 수락된 누적 횟수 조회
    long countByReportedUser_UserIdAndReportStatus(Long userId, ReportStatus reportStatus);

    // 특정 사용자가 받은 수락된 신고 목록 조회
    List<Report> findByReportedUser_UserIdAndReportStatus(Long userId, ReportStatus reportStatus);

    // 관리자 신고 목록 조회
    @Query("""
            SELECT r FROM Report r
            WHERE (:reportStatus IS NULL OR r.reportStatus = :reportStatus)
            AND (:targetType IS NULL OR r.targetType = :targetType)
            AND (
                :keyword IS NULL
                OR r.reporter.username LIKE %:keyword%
                OR r.reporter.nickname LIKE %:keyword%
                OR r.reportedUser.username LIKE %:keyword%
                OR r.reportedUser.nickname LIKE %:keyword%
                OR r.reason LIKE %:keyword%
            )
            """)
    Page<Report> searchAdminReports(
            @Param("reportStatus") ReportStatus reportStatus,
            @Param("targetType") TargetType targetType,
            @Param("keyword") String keyword,
            Pageable pageable);

    long countByReportStatus(ReportStatus reportStatus);
}
