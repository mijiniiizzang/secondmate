package com.example.secondmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.secondmate.entity.ChatRoom;
import com.example.secondmate.entity.Review;
import com.example.secondmate.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // 같은 채팅방에서 같은 사람이 같은 상대에게 리뷰를 이미 썼는지 확인(거래 완료 건 당 하나의 리뷰만 등록 가능)
    boolean existsByChatRoomAndReviewerAndReviewee(ChatRoom chatRoom, User reviewer, User reviewee);

    // 어떤 사용자가 받은 리뷰 목록 조회
    List<Review> findByRevieweeOrderByRegDateDesc(User reviewee);

    List<Review> findByReviewerOrderByRegDateDesc(User reviewer);

    // 받은 리뷰 개수 조회
    long countByReviewee(User reviewee);
}
