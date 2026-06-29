package com.example.secondmate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDTO {
    private Long imageId;
    private String imagePath;
    private String imageRealName;
    private String imageChgName;
    private boolean mainImage;
}
