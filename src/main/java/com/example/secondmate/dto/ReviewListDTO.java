package com.example.secondmate.dto;

import java.time.LocalDateTime;

import com.example.secondmate.common.ReviewType;
import com.example.secondmate.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewListDTO {
    private Long reviewId;
    private Long productId;
    private String productTitle;
    private String reviewerNickname;
    private String revieweeNickname;
    private ReviewType reviewType;
    private String content;
    private LocalDateTime regDate;

    public static ReviewListDTO from(Review review) {
        return ReviewListDTO.builder()
                            .reviewId(review.getReviewId())
                            .productId(review.getProduct().getProductId())
                            .productTitle(review.getProduct().getTitle())
                            .reviewerNickname(review.getReviewer().getNickname())
                            .revieweeNickname(review.getReviewee().getNickname())
                            .reviewType(review.getReviewType())
                            .content(review.getContent())
                            .regDate(review.getRegDate())
                            .build();
    }
}
