package com.example.secondmate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.secondmate.dto.WishlistDTO;
import com.example.secondmate.entity.Product;
import com.example.secondmate.entity.User;
import com.example.secondmate.entity.Wishlist;
import com.example.secondmate.repository.ProductRepository;
import com.example.secondmate.repository.UserRepository;
import com.example.secondmate.repository.WishlistRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 찜 목록 조회
    public List<WishlistDTO> getWishlistList(Long userId) {
        List<Wishlist> wishlistList = wishlistRepository.findByUser_UserId(userId);

        return wishlistList.stream()
                           .map(wishlist -> WishlistDTO.builder()
                           .wishId(wishlist.getWishId())
                           .userId(wishlist.getUser().getUserId())
                           .productId(wishlist.getProduct().getProductId())
                           .regDate(wishlist.getRegDate())
                           .build())
                           .toList();
    }

    // 특정 상품 찜했는지 확인하기
    public boolean isWished(Long userId, Long productId) {
        return wishlistRepository.existsByUser_UserIdAndProduct_ProductId(userId, productId);
    }

    // 찜 추가하기
    public void addWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

        Wishlist wishlist = Wishlist.builder()
                                    .user(user)
                                    .product(product)
                                    .build();
        wishlistRepository.save(wishlist);
    }

    // 찜 삭제하기
    public void deleteWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByUser_UserIdAndProduct_ProductId(userId, productId);
    }

    // 이미 찜했으면 삭제, 아니면 추가
    public boolean toggleWishlist(Long userId, Long productId) {
        boolean wished = wishlistRepository.existsByUser_UserIdAndProduct_ProductId(userId, productId);

        if(wished) {
            deleteWishlist(userId, productId);
            return false;
        }
        addWishlist(userId, productId);
        return true;
    }

    // 특정 상품의 전체 찜 수
    public long getWishlistCount(Long productId) {
        return wishlistRepository.countByProduct_ProductId(productId);
    }
}
