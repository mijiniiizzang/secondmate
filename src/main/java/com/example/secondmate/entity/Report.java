package com.example.secondmate.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.secondmate.common.ReportStatus;
import com.example.secondmate.common.TargetType;

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
public class Report {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long reportId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="reporter_id", nullable=false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TargetType targetType;

    private Long targetId;
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ReportStatus reportStatus;

    @CreationTimestamp
    private LocalDateTime regDate;

}
