package com.example.secondmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.secondmate.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    // 오름차순으로 이미지 불러오기
    List<ProductImage> findByProduct_ProductIdOrderByImageIdAsc(Long productId);
}
