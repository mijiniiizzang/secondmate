package com.example.secondmate.dto;

import java.time.LocalDateTime;

import com.example.secondmate.common.TradeStatus;
import com.example.secondmate.entity.Trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeDTO {
    private Long tradeId;

    private Long productId;
    private String productTitle;
    private String productName;
    private Long price;
    private TradeStatus tradeStatus;

    private Long sellerId;
    private String sellerNickname;

    private Long buyerId;
    private String buyerNickname;

    private LocalDateTime tradeDate;
    private LocalDateTime completedAt;

    private Long chatRoomId;
    private Long revieweeId;
    private String revieweeNickname;
    private boolean canWriteReview;
    private boolean reviewWritten;

    public static TradeDTO fromEntity(Trade trade) {
        return TradeDTO.builder()
                .tradeId(trade.getTradeId())

                .productId(trade.getProduct().getProductId())
                .productTitle(trade.getProduct().getTitle())
                .productName(trade.getProduct().getName())
                .price(trade.getProduct().getPrice())
                .tradeStatus(trade.getProduct().getTradeStatus())

                .sellerId(trade.getSeller().getUserId())
                .sellerNickname(trade.getSeller().getNickname())

                .buyerId(trade.getBuyer().getUserId())
                .buyerNickname(trade.getBuyer().getNickname())

                .tradeDate(trade.getTradeDate())
                .completedAt(trade.getCompletedAt())

                .chatRoomId(null)
                .revieweeId(null)
                .revieweeNickname(null)
                .canWriteReview(false)
                .reviewWritten(false)
                .build();
    }
}
