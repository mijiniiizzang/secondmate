package com.example.secondmate.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.secondmate.common.ProductCategory;
import com.example.secondmate.common.TradeStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long productId;
    private String title;
    private String name;
    private String writer;
    private Integer writerMateScore;
    private Long price;
    private ProductCategory category;
    private String content;
    private String city;
    private String gu;
    private Double latitude;
    private Double longitude;
    private TradeStatus tradeStatus;
    private boolean hidden;
    private String hiddenReason;
    private LocalDateTime regDate;
    private int commentCount;
    private boolean isWished;
    private Integer mainImageIndex;

    private List<ProductImageDTO> imageList;
}
