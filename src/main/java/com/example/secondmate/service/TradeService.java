package com.example.secondmate.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondmate.dto.TradeDTO;
import com.example.secondmate.repository.TradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class TradeService {
    private final TradeRepository tradeRepository;

    // 내 판매 거래내역 조회
    public List<TradeDTO> getSalesTrades(Long userId) {
        return tradeRepository.findBySeller_UserId(userId)
                              .stream()
                              .map(TradeDTO::fromEntity)
                              .toList();
    }

    // 내 구매 거래내역 조회
    public List<TradeDTO> getPurchaseTrades(Long userId) {
        return tradeRepository.findByBuyer_UserId(userId)
                              .stream()
                              .map(TradeDTO::fromEntity)
                              .toList();
    }
}
