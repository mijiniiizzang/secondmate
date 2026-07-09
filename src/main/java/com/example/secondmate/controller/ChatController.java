package com.example.secondmate.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.secondmate.dto.ChatMessageDTO;
import com.example.secondmate.dto.ChatRoomDTO;
import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.ChatService;
import com.example.secondmate.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    
    private final ChatService chatService;
    private final ReviewService reviewService;

    // 채팅방 생성
    @PostMapping("/room")
    public String createRoom(@RequestParam Long productId, @AuthenticationPrincipal AccountDetails accountDetails) {
        if (accountDetails == null) {
            return "redirect:/auth/login?redirectUrl=/product/detail?productId=" + productId;
        }

        Long roomId = chatService.createOrGetRoom(productId, accountDetails.getUserId());

        return "redirect:/chat/room/" + roomId; 
    }

    // 채팅방 목록 가져오기
    @GetMapping("/list")
    public String chatList(Model model, @AuthenticationPrincipal AccountDetails accountDetails) {
        if (accountDetails == null) {
            return "redirect:/auth/login?redirectUrl=/chat/list";
        }

        List<ChatRoomDTO> chatRooms = chatService.getMyRooms(accountDetails.getUserId());

        model.addAttribute("chatRooms", chatRooms);

        return "chat/list";
    }

    // 특정 채팅방 가져오기
    @GetMapping("/room/{roomId}")
    public String chatRoom(@PathVariable Long roomId, Model model, @AuthenticationPrincipal AccountDetails accountDetails) {
        if (accountDetails == null) {
            return "redirect:/auth/login?redirectUrl=/chat/room/" + roomId;
        }

        Long loginUserId = accountDetails.getUserId();

        ChatRoomDTO room = chatService.getRoom(roomId, loginUserId);

        List<ChatMessageDTO> messages = chatService.getMessages(roomId, accountDetails.getUserId());

        boolean canWriteReview = reviewService.canWriteReview(roomId, loginUserId);

        model.addAttribute("room", room);
        model.addAttribute("messages", messages);
        model.addAttribute("canWriteReview", canWriteReview);

        return "chat/room";
    }

    // 채팅 보내기
    @PostMapping("/room/{roomId}/message")
    public String sendMessage(@PathVariable Long roomId, @RequestParam String content, @AuthenticationPrincipal AccountDetails accountDetails) {
        if (accountDetails == null) {
            return "redirect:/auth/login?redirectUrl=/chat/room/" + roomId;
        }

        chatService.sendMessage(roomId, accountDetails.getUserId(), content);
        return "redirect:/chat/room/" + roomId;
    }

    // header.html에서 쓸 JSON API
    // 헤더 채팅 패널 - 채팅방 목록 전체 조회
    @GetMapping("/api/rooms")
    @ResponseBody
    public List<ChatRoomDTO> getChatRooms(@AuthenticationPrincipal AccountDetails accountDetails) {
        return chatService.getMyRooms(accountDetails.getUserId());
    }

    // 헤더 채팅 패널 - 선택한 채팅방 정보 조회
    @GetMapping("/api/rooms/{roomId}")
    @ResponseBody
    public ChatRoomDTO getChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal AccountDetails accountDetails) {
        return chatService.getRoom(roomId, accountDetails.getUserId());
    }

    // 헤더 채팅 패널 - 선택한 채팅방 이전 메세지 조회
    @GetMapping("/api/rooms/{roomId}/messages")
    @ResponseBody
    public List<ChatMessageDTO> getChatMessages(@PathVariable Long roomId, @AuthenticationPrincipal AccountDetails accountDetails) {
        return chatService.getMessages(roomId, accountDetails.getUserId());
    }

    // 채팅방 나가기
    @PostMapping("/api/rooms/{roomId}/leave")
    @ResponseBody
    public void leaveChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal AccountDetails accountDetails) {
        chatService.leaveRoom(roomId, accountDetails.getUserId());
    }

    // 전체 안 읽은 채팅 메세지 수
    @GetMapping("/api/unread-count")
    @ResponseBody
    public long getUnreadChatCount(@AuthenticationPrincipal AccountDetails accountDetails) {
        return chatService.getUnreadCount(accountDetails.getUserId());
    }

    // 거래 상태 변경
    @PostMapping("/api/rooms/{roomId}/trade-status")
    @ResponseBody
    public void changeTradeStatus(@PathVariable Long roomId, @RequestParam int tradeStatusId, @AuthenticationPrincipal AccountDetails accountDetails) {
        chatService.changeTradeStatus(roomId, accountDetails.getUserId(), tradeStatusId);
    }
}
