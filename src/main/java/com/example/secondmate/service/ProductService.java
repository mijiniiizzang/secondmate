package com.example.secondmate.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.secondmate.common.ProductCategory;
import com.example.secondmate.common.TradeStatus;
import com.example.secondmate.dto.ProductDTO;
import com.example.secondmate.dto.ProductImageDTO;
import com.example.secondmate.entity.Product;
import com.example.secondmate.entity.ProductImage;
import com.example.secondmate.entity.User;
import com.example.secondmate.repository.CommentRepository;
import com.example.secondmate.repository.ProductRepository;
import com.example.secondmate.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 상품 등록
    @Transactional
    public int saveProduct(ProductDTO productDTO, Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Product product = Product.builder()
                                 .user(user)
                                 .title(productDTO.getTitle())
                                 .name(productDTO.getName())
                                 .price(productDTO.getPrice())
                                 .category(productDTO.getCategory())
                                 .content(productDTO.getContent())
                                 .city(productDTO.getCity())
                                 .gu(productDTO.getGu())
                                 .latitude(productDTO.getLatitude())
                                 .longitude(productDTO.getLongitude())
                                 .tradeStatus(TradeStatus.ON_SALE)
                                 .build();
        
        int mainImageIndex = productDTO.getMainImageIndex();
        List<ProductImage> images = productDTO.getImageList().stream()
                                              .map(productImageDTO -> ProductImage.builder()
                                              .imagePath(productImageDTO.getImagePath())
                                              .imageRealName(productImageDTO.getImageRealName())
                                              .imageChgName(productImageDTO.getImageChgName())                                              
                                              .mainImage(false)
                                              .product(product)
                                              .build())
                                              .collect(Collectors.toList());
        images.get(mainImageIndex).setMainImage(true);
        product.getImageList().addAll(images);
        productRepository.save(product);
        return 1;
    }

    // 상품 검색 + 조회
    public Page<ProductDTO> search(String keyword,
                                   List<ProductCategory> categories,
                                   String city,
                                   String gu,
                                   TradeStatus tradeStatus,
                                   Long minPrice,
                                   Long maxPrice,
                                   Pageable pageable) {
        Page<Product> products = productRepository.searchProducts(
            keyword,
            categories,
            city,
            gu,
            tradeStatus,
            minPrice,
            maxPrice,
            pageable
        );
        return products.map(this::toDTO);
    }

    // 상품 상세 조회
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));
        return toDTO(product);
    }

    // 상품 수정
    @Transactional
    public int updateProduct(Long productId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));
        product.setTitle(productDTO.getTitle());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setContent(productDTO.getContent());
        product.setCity(productDTO.getCity());
        product.setGu(productDTO.getGu());
        product.setLatitude(productDTO.getLatitude());
        product.setLongitude(productDTO.getLongitude());

        product.getImageList().clear();
        if(productDTO.getImageList() != null) {
            List<ProductImage> images = productDTO.getImageList().stream()
                                              .map(productImageDTO -> ProductImage.builder()
                                              .imagePath(productImageDTO.getImagePath())
                                              .imageRealName(productImageDTO.getImageRealName())
                                              .imageChgName(productImageDTO.getImageChgName())
                                              .product(product)
                                              .build())
                                              .collect(Collectors.toList());
            product.getImageList().addAll(images);
        }

        productRepository.save(product);
        return 1;

    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId) {
        commentRepository.deleteByProduct_ProductId(productId);
        productRepository.deleteById(productId);
    }

    // 거래 상태 변경
    public int updateTradeStatus(Long productId, TradeStatus tradeStatus) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));
        product.setTradeStatus(tradeStatus);

        if (tradeStatus == TradeStatus.SOLD) {
            product.setSoldAt(LocalDateTime.now());
        } else {
            product.setSoldAt(null);
        }
        
        productRepository.save(product);
        return 1;
    }

    // 상품 이미지 저장 및 삭제

    // Product -> ProductDTO 변경
    private ProductDTO toDTO(Product product) {
        List<ProductImageDTO> imageDTOs = product.getImageList().stream()
                                                 .map(image -> ProductImageDTO.builder()
                                                 .imageId(image.getImageId())
                                                 .imagePath(image.getImagePath())
                                                 .imageRealName(image.getImageRealName())
                                                 .imageChgName(image.getImageChgName())
                                                 .mainImage(image.isMainImage())
                                                 .build())
                                                 .collect(Collectors.toList());
        return ProductDTO.builder()
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
                         .imageList(imageDTOs)
                         .build();
    }

    // 작성자 확인 메소드
    public boolean isProductOwner(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

        return product.getUser().getUserId().equals(userId);
    }

    // 내가 올린 상품 목록
    public Page<ProductDTO> getMyProducts(Long userId, Pageable pageable) {
        Page<Product> products = productRepository.findByUser_UserId(userId, pageable);

        return products.map(this::toDTO);
    }
}
