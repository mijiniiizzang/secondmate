package com.example.secondmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.secondmate.entity.Trade;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    // 판매 내역
    List<Trade> findBySeller_UserId(Long sellerId);

    // 구매 내역
    List<Trade> findByBuyer_UserId(Long buyerId);

    // 거래 정보 조회
    Optional<Trade> findByProduct_ProductId(Long productId);
}
