package com.example.secondmate.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondmate.dto.ChatMessageDTO;
import com.example.secondmate.dto.ChatSendDTO;
import com.example.secondmate.entity.ChatRoom;
import com.example.secondmate.repository.ChatRoomRepository;
import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    
    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    @Transactional
    public void sendMessage(ChatSendDTO chatSendDTO, Principal principal) {
        if (!(principal instanceof Authentication authentication)
            || !(authentication.getPrincipal() instanceof AccountDetails accountDetails)) {
            throw new IllegalArgumentException("로그인 정보를 찾을 수 없습니다.");
        }

        Long senderId = accountDetails.getUserId();

        ChatMessageDTO message = chatService.sendMessage(chatSendDTO.getRoomId(), senderId, chatSendDTO.getContent());

        ChatRoom chatRoom = chatRoomRepository.findById(chatSendDTO.getRoomId())
                                              .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방"));

        // spring security에서 로그인 사용자를 식별하기 위한
        String buyerUsername = chatRoom.getBuyer().getUsername();
        String sellerUsername = chatRoom.getProduct().getUser().getUsername();

        messagingTemplate.convertAndSendToUser(buyerUsername, "/queue/chat", message);

        messagingTemplate.convertAndSendToUser(sellerUsername, "/queue/chat", message);
    }
}
