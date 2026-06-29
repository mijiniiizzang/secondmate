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
public class CommentDTO {
    private Long commentId;
    private String writer;
    private String content;
    private LocalDateTime createdAt;
    private Long productId;
    private Long userId;
}
