package com.example.secondmate.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondmate.common.TradeStatus;
import com.example.secondmate.dto.TradeDTO;
import com.example.secondmate.entity.ChatRoom;
import com.example.secondmate.entity.Trade;
import com.example.secondmate.entity.User;
import com.example.secondmate.repository.ChatRoomRepository;
import com.example.secondmate.repository.ReviewRepository;
import com.example.secondmate.repository.TradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {

    private final TradeRepository tradeRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ReviewRepository reviewRepository;

    // 내 판매 거래내역 조회
    public List<TradeDTO> getSalesTrades(Long userId) {
        return tradeRepository.findBySeller_UserId(userId)
                .stream()
                .map(trade -> toTradeDTO(trade, userId))
                .toList();
    }

    // 내 구매 거래내역 조회
    public List<TradeDTO> getPurchaseTrades(Long userId) {
        return tradeRepository.findByBuyer_UserId(userId)
                .stream()
                .map(trade -> toTradeDTO(trade, userId))
                .toList();
    }

    private TradeDTO toTradeDTO(Trade trade, Long loginUserId) {

        TradeDTO tradeDTO = TradeDTO.fromEntity(trade);

        Long productId = trade.getProduct().getProductId();
        Long buyerId = trade.getBuyer().getUserId();

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByProduct_ProductIdAndBuyer_UserId(productId,
                buyerId);

        if (optionalChatRoom.isEmpty()) {
            tradeDTO.setChatRoomId(null);
            tradeDTO.setRevieweeId(null);
            tradeDTO.setRevieweeNickname(null);
            tradeDTO.setCanWriteReview(false);
            tradeDTO.setReviewWritten(false);

            return tradeDTO;
        }

        ChatRoom chatRoom = optionalChatRoom.get();

        User seller = trade.getSeller();
        User buyer = trade.getBuyer();

        User reviewer;
        User reviewee;

        if (loginUserId.equals(seller.getUserId())) {
            reviewer = seller;
            reviewee = buyer;
        } else if (loginUserId.equals(buyer.getUserId())) {
            reviewer = buyer;
            reviewee = seller;
        } else {
            tradeDTO.setChatRoomId(chatRoom.getRoomId());
            tradeDTO.setRevieweeId(null);
            tradeDTO.setRevieweeNickname(null);
            tradeDTO.setCanWriteReview(false);
            tradeDTO.setReviewWritten(false);

            return tradeDTO;
        }

        boolean reviewWritten = reviewRepository.existsByChatRoomAndReviewerAndReviewee(chatRoom, reviewer, reviewee);

        boolean canWriteReview = trade.getProduct().getTradeStatus() == TradeStatus.SOLD && !reviewWritten;

        tradeDTO.setChatRoomId(chatRoom.getRoomId());
        tradeDTO.setRevieweeId(reviewee.getUserId());
        tradeDTO.setRevieweeNickname(reviewee.getNickname());
        tradeDTO.setReviewWritten(reviewWritten);
        tradeDTO.setCanWriteReview(canWriteReview);

        return tradeDTO;
    }
}