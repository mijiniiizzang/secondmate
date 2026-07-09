package com.example.secondmate.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondmate.common.ReviewType;
import com.example.secondmate.common.TradeStatus;
import com.example.secondmate.dto.ReviewCreateDTO;
import com.example.secondmate.dto.ReviewListDTO;
import com.example.secondmate.entity.ChatRoom;
import com.example.secondmate.entity.Product;
import com.example.secondmate.entity.Review;
import com.example.secondmate.entity.User;
import com.example.secondmate.repository.ChatRoomRepository;
import com.example.secondmate.repository.ReviewRepository;
import com.example.secondmate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 리뷰 등록
    public void writeReview(Long reviewerId, ReviewCreateDTO reviewCreateDTO) {
        ChatRoom chatRoom = chatRoomRepository.findById(reviewCreateDTO.getChatRoomId())
                                              .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        User reviewer = userRepository.findById(reviewerId)
                                      .orElseThrow(() -> new IllegalArgumentException("리뷰 작성자 없음"));

        User reviewee = userRepository.findById(reviewCreateDTO.getRevieweeId())
                                      .orElseThrow(() -> new IllegalArgumentException("리뷰 대상자 없음"));

        Product product = chatRoom.getProduct();

        if (product.getTradeStatus() != TradeStatus.SOLD) {
            throw new IllegalArgumentException("거래 완료된 상품만 리뷰를 작성할 수 있음");
        }

        User seller = product.getUser();
        User buyer = chatRoom.getBuyer();

        boolean isReviewerBuyer = buyer.getUserId().equals(reviewer.getUserId());
        boolean isReviewerSeller = seller.getUserId().equals(reviewer.getUserId());

        if (!isReviewerBuyer && !isReviewerSeller) {
            throw new IllegalArgumentException("거래 당사자만 리뷰 작성 가능");
        }

        boolean isRevieweeBuyer = buyer.getUserId().equals(reviewee.getUserId());
        boolean isRevieweeSeller = seller.getUserId().equals(reviewee.getUserId());

        if (!isRevieweeBuyer && !isRevieweeSeller) {
            throw new IllegalArgumentException("거래 상대에게만 리뷰 작성 가능");
        }

        if (reviewer.getUserId().equals(reviewee.getUserId())) {
            throw new IllegalArgumentException("자기 자신에게 리뷰 작성 불가");
        }

        if (reviewRepository.existsByChatRoomAndReviewerAndReviewee(chatRoom, reviewer, reviewee)) {
            throw new IllegalArgumentException("이미 리뷰 작성 완료");
        }

        Review review = Review.builder()
                              .product(product)
                              .chatRoom(chatRoom)
                              .reviewer(reviewer)
                              .reviewee(reviewee)
                              .reviewType(reviewCreateDTO.getReviewType())
                              .content(reviewCreateDTO.getContent())
                              .build();

        reviewRepository.save(review);
        updateMateScore(reviewee, reviewCreateDTO.getReviewType());
    }

    private void updateMateScore(User reviewee, ReviewType reviewType) {
        int score = reviewee.getMateScore();

        if (reviewType == ReviewType.GOOD) {
            score += 2;
        } else if (reviewType == ReviewType.BAD) {
            score -= 5;
        }

        if (score > 100) {
            score = 100;
        }
        if (score < 0) {
            score = 0;
        }

        reviewee.setMateScore(score);
    }

    @Transactional(readOnly=true)
    public List<ReviewListDTO> getReceivedReviews(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return reviewRepository.findByRevieweeOrderByRegDateDesc(user)
                               .stream()
                               .map(ReviewListDTO::from)
                               .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewListDTO> getWrittenReviews(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return reviewRepository.findByRevieweeOrderByRegDateDesc(user)
                               .stream()
                               .map(ReviewListDTO::from)   
                               .toList();
    }

    @Transactional(readOnly=true)
    public long countReceivedReviews(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return reviewRepository.countByReviewee(user);
    }

    @Transactional(readOnly=true)
    public boolean canWriteReview(Long chatRoomId, Long loginUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                                              .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        Product product = chatRoom.getProduct();

        if (product.getTradeStatus() != TradeStatus.SOLD) {
            return false;
        }

        User seller = product.getUser();
        User buyer = chatRoom.getBuyer();

        User reviewer;
        User reviewee;

        if (seller.getUserId().equals(loginUserId)) {
            reviewer = seller;
            reviewee = buyer;
        } else if (buyer.getUserId().equals(loginUserId)) {
            reviewer = buyer;
            reviewee = seller;
        } else {
            return false;
        }

        return !reviewRepository.existsByChatRoomAndReviewerAndReviewee(chatRoom, reviewer, reviewee);
    }

}
