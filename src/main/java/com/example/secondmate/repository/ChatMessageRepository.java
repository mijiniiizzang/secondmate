package com.example.secondmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.secondmate.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 채팅 메세지 조회
    List<ChatMessage> findByChatRoom_RoomIdOrderBySentAtAsc(Long roomId);
}
