package com.example.secondmate.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.secondmate.common.InquiryStatus;
import com.example.secondmate.common.InquiryType;
import com.example.secondmate.entity.Inquiry;
import com.example.secondmate.entity.User;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    // 사용자 문의 내역
    Page<Inquiry> findByUserOrderByRegDateDesc(User user, Pageable pageable);

    // 관리자 문의 내역 전체 조회
    Page<Inquiry> findAllByOrderByRegDateDesc(Pageable pageable);

    // 관리자 문의 필터 적용 검색
    @Query("""
            SELECT i
            FROM Inquiry i
            WHERE (:inquiryStatus IS NULL OR i.inquiryStatus = :inquiryStatus)
              AND (:inquiryTypes IS NULL OR i.inquiryType IN :inquiryTypes)
            """)
    Page<Inquiry> searchAdminInquiries(
            @Param("inquiryStatus") InquiryStatus inquiryStatus,
            @Param("inquiryTypes") List<InquiryType> inquiryTypes,
            Pageable pageable);

    // 답변 대기 수
    long countByInquiryStatus(InquiryStatus inquiryStatus);

    // 사용자 문의 수
    long countByUser_UserId(Long userId);
}
