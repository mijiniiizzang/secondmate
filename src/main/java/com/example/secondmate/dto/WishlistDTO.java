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
public class WishlistDTO {
    private Long wishId;
    private Long productId;
    private Long userId;
    private LocalDateTime regDate;

    private ProductDTO productDTO;
}
