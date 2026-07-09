package com.example.secondmate.dto;

import com.example.secondmate.common.ReviewType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateDTO {
    private Long chatRoomId;
    private Long revieweeId;
    private ReviewType reviewType;
    private String content;
}
