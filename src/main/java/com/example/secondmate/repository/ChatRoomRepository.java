package com.example.secondmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.secondmate.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 채팅방 찾기
    Optional<ChatRoom> findByProduct_ProductIdAndBuyer_UserId(Long productId, Long userId);

    // 내 채팅방 찾기(구매자)
    List<ChatRoom> findByBuyer_UserId(Long userId);

    // 내 채팅방 찾기(판매자)
    List<ChatRoom> findByProduct_User_UserId(Long userId);
}
