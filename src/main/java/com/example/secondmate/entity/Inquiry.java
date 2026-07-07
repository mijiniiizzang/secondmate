package com.example.secondmate.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.secondmate.common.InquiryStatus;
import com.example.secondmate.common.InquiryType;

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
public class Inquiry {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inquiryId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(nullable=false)
    private String title;
    @Column(nullable=false, columnDefinition="TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private InquiryStatus inquiryStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private InquiryType inquiryType;

    @Column(columnDefinition="TEXT")
    private String answer;

    private LocalDateTime answeredAt; // 관리자 답변 등록 시간

    @CreationTimestamp
    private LocalDateTime regDate; // 사용자가 문의한 시간
}
