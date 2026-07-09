package com.example.secondmate.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.secondmate.common.ProductCategory;
import com.example.secondmate.dto.ProductDTO;
import com.example.secondmate.dto.ProductImageDTO;
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
    public Page<WishlistDTO> getWishlistList(Long userId, List<ProductCategory> categories, Pageable pageable) {
        Page<Wishlist> wishlists;

        if (categories == null || categories.isEmpty()) {
            wishlists = wishlistRepository.findByUser_UserId(userId, pageable);
        } else {
            wishlists = wishlistRepository.findByUser_UserIdAndProduct_CategoryIn(userId, categories, pageable);
        }

        return wishlists.map(this::toDTO);
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

        if (wished) {
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

    // Entity -> toDTO
    private WishlistDTO toDTO(Wishlist wishlist) {
        Product product = wishlist.getProduct();

        List<ProductImageDTO> imageList = product.getImageList()
                .stream()
                .map(image -> ProductImageDTO.builder()
                        .imageId(image.getImageId())
                        .imagePath(image.getImagePath())
                        .imageRealName(image.getImageRealName())
                        .imageChgName(image.getImageChgName())
                        .mainImage(image.isMainImage())
                        .build())
                .collect(Collectors.toList());

        ProductDTO productDTO = ProductDTO.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .name(product.getName())
                .writer(product.getUser().getNickname())
                .price(product.getPrice())
                .category(product.getCategory())
                .content(product.getContent())
                .city(product.getCity())
                .gu(product.getGu())
                .latitude(product.getLatitude())
                .longitude(product.getLongitude())
                .tradeStatus(product.getTradeStatus())
                .hidden(product.isHidden())
                .hiddenReason(product.getHiddenReason())
                .regDate(product.getRegDate())
                .commentCount(product.getCommentCount())
                .imageList(imageList)
                .build();

        return WishlistDTO.builder()
                .wishId(wishlist.getWishId())
                .userId(wishlist.getUser().getUserId())
                .productId(product.getProductId())
                .regDate(wishlist.getRegDate())
                .productDTO(productDTO)
                .build();
    }

    // 특정 사용자의 찜 갯수
    public long getMyWishlistCount(Long userId) {
        return wishlistRepository.countByUser_UserId(userId);
    }

}
