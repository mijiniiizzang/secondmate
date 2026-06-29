package com.example.secondmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.secondmate.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    // 특정 회원의 찜 목록 불러오기
    List<Wishlist> findByUser_UserId(Long userId);

    // 찜 했나?
    boolean existsByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

    // 찜 삭제
    void deleteByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

    // 특정 상품의 전체 찜 수
    long countByProduct_ProductId(Long productId);
}
