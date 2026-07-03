package com.example.secondmate.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.secondmate.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 채팅 메세지 조회
    List<ChatMessage> findByChatRoom_RoomIdOrderBySentAtAsc(Long roomId);

    // 채팅방 나간 시간
    List<ChatMessage> findByChatRoom_RoomIdAndSentAtAfterOrderBySentAtAsc(Long roomId, LocalDateTime leftAt);

    // 채팅방 나가기
    void deleteByChatRoom_RoomId(Long roomId);

    // 나간 뒤 상대 메시지가 있으면 채팅방 다시 표시
    boolean existsByChatRoom_RoomIdAndSender_UserIdNotAndSentAtAfter(Long roomId, Long userId, LocalDateTime leftAt);

    // 채팅방의 마지막 메세지 조회
    Optional<ChatMessage> findTopByChatRoom_RoomIdOrderBySentAtDesc(Long roomId);

    // 특정 사용자가 읽지 않은 메세지 수
    long countByChatRoom_RoomIdAndSender_UserIdNotAndReadYn(Long roomId, Long userId, String readYn);
}
