package com.example.secondmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.secondmate.common.TargetType;
import com.example.secondmate.entity.Report;
import com.example.secondmate.common.ReportStatus;


public interface ReportRepository extends JpaRepository<Report, Long> {
    // 신고 이력이 있는지?
    boolean existsByReporter_UserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    // 신고 상태 조회
    List<Report> findByReportStatus(ReportStatus reportStatus);
}
