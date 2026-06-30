package com.example.secondmate.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {
    private Long reportId;
    private Long reporterId;
    private String targetType;
    private Long targetId;
    private String reportStatus;
    private String reportType;
    private String reason;
    private LocalDateTime regDate;
}
