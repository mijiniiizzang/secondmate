package com.example.secondmate.dto;

import java.time.LocalDateTime;

import com.example.secondmate.common.InquiryStatus;
import com.example.secondmate.common.InquiryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryDTO {
    private Long inquiryId;

    private Long userId;
    private String username;
    private String nickname;

    private String title;
    private String content;

    private InquiryType inquiryType;
    private InquiryStatus inquiryStatus;

    private String answer;
    private LocalDateTime answeredAt;
    private LocalDateTime regDate;
}
