package com.example.secondmate.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.secondmate.common.ProductCategory;
import com.example.secondmate.common.TradeStatus;
import com.example.secondmate.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 특정 회원이 등록한 상품 검색
    Page<Product> findByUser_UserId(Long userId, Pageable pageable);
    
    // 상품 이름으로 검색
    @Query("""
            SELECT p FROM Product p
            where (:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%'))
            AND (:categories IS NULL OR p.category IN :categories)
            AND (:city IS NULL OR p.city = :city)
            AND (:gu IS NULL OR p.gu = :gu)
            AND (:tradeStatus IS NULL OR p.tradeStatus = :tradeStatus)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    Page<Product> searchProducts(
        @Param("keyword") String keyword,
        @Param("categories") List<ProductCategory> categories,
        @Param("city") String city,
        @Param("gu") String gu,
        @Param("tradeStatus") TradeStatus tradeStatus,
        @Param("minPrice") Long minPrice,
        @Param("maxPrice") Long maxPrice,
        Pageable pageable
    );

    // [관리자]용 검색
    @Query("""
            SELECT p FROM Product p
            WHERE (:tradeStatus IS NULL OR p.tradeStatus = :tradeStatus)
            AND (:categories IS NULL OR p.category IN :categories)
            AND (
                :keyword IS NULL
                OR p.title LIKE %:keyword%
                OR p.name LIKE %:keyword%
                OR p.user.username LIKE %:keyword%
                OR p.user.nickname LIKE %:keyword%
            )
            """)
    Page<Product> searchAdminProducts(
        @Param("tradeStatus") TradeStatus tradeStatus,
        @Param("categories") List<ProductCategory> categories,
        @Param("keyword") String keyword,
        Pageable pageable
    );

    long countByRegDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 내가 올린 상품 개수 검색
    long countByUser_UserId(Long userId);
}
