package com.example.secondmate.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondmate.common.RoomStatus;
import com.example.secondmate.common.TradeStatus;
import com.example.secondmate.dto.ChatMessageDTO;
import com.example.secondmate.dto.ChatRoomDTO;
import com.example.secondmate.entity.ChatMessage;
import com.example.secondmate.entity.ChatRoom;
import com.example.secondmate.entity.Product;
import com.example.secondmate.entity.Trade;
import com.example.secondmate.entity.User;
import com.example.secondmate.repository.ChatMessageRepository;
import com.example.secondmate.repository.ChatRoomRepository;
import com.example.secondmate.repository.ProductRepository;
import com.example.secondmate.repository.TradeRepository;
import com.example.secondmate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 채팅방 생성
    @Transactional
    public Long createOrGetRoom(Long productId, Long buyerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

        if (product.getUser().getUserId().equals(buyerId)) {
            throw new IllegalArgumentException("내 상품에는 채팅할 수 없습니다.");
        }

        Optional<ChatRoom> existingRoom = chatRoomRepository.findByProduct_ProductIdAndBuyer_UserId(productId, buyerId);

        if (existingRoom.isPresent()) {
            return existingRoom.get().getRoomId();
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        ChatRoom chatRoom = ChatRoom.builder()
                .product(product)
                .buyer(buyer)
                .roomStatus(RoomStatus.ACTIVE)
                .build();

        return chatRoomRepository.save(chatRoom).getRoomId();
    }

    // 채팅방 목록 가져오기
    public List<ChatRoomDTO> getMyRooms(Long userId) {
        // 내가 구매자일 경우
        List<ChatRoom> buyerRooms = chatRoomRepository.findByBuyer_UserId(userId);
        // 내가 판매자일 경우
        List<ChatRoom> sellerRooms = chatRoomRepository.findByProduct_User_UserId(userId);

        List<ChatRoom> rooms = new ArrayList<>();
        rooms.addAll(buyerRooms);
        rooms.addAll(sellerRooms);

        return rooms.stream()
                .filter(room -> canShowRoom(room, userId))
                .sorted(
                        Comparator.comparing(
                                ChatRoom::getCreatedAt,
                                Comparator.reverseOrder()))
                .map(room -> toRoomDTO(room, userId))
                .toList();
    }

    public ChatRoomDTO getRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = getAuthorizedRoom(roomId, userId);

        return toRoomDTO(chatRoom, userId);
    }

    // 채팅창 메세지 가져오기
    @Transactional
    public List<ChatMessageDTO> getMessages(Long roomId, Long userId) {
        ChatRoom chatRoom = getAuthorizedRoom(roomId, userId);

        LocalDateTime leftAt;

        if (chatRoom.getBuyer().getUserId().equals(userId)) {
            leftAt = chatRoom.getBuyerLeftAt();
        } else {
            leftAt = chatRoom.getSellerLeftAt();
        }

        List<ChatMessage> messages;

        if (leftAt == null) {
            messages = chatMessageRepository.findByChatRoom_RoomIdOrderBySentAtAsc(roomId);
        } else {
            messages = chatMessageRepository.findByChatRoom_RoomIdAndSentAtAfterOrderBySentAtAsc(roomId, leftAt);
        }

        for (ChatMessage message : messages) {
            if (!message.getSender().getUserId().equals(userId) && "N".equals(message.getReadYn())) {
                message.setReadYn("Y");

                ChatMessageDTO readMessage = ChatMessageDTO.builder()
                        .messageId(message.getMessageId())
                        .roomId(roomId)
                        .readYn("Y")
                        .build();

                messagingTemplate.convertAndSendToUser(
                        message.getSender().getUsername(),
                        "/queue/chat-read",
                        readMessage);
            }
        }

        return messages.stream()
                .map(message -> ChatMessageDTO.builder()
                        .messageId(message.getMessageId())
                        .roomId(roomId)
                        .senderId(message.getSender().getUserId())
                        .senderNickname(message.getSender().getNickname())
                        .content(message.getContent())
                        .readYn(message.getReadYn())
                        .sentAt(message.getSentAt())
                        .mine(message.getSender().getUserId().equals(userId))
                        .build())
                .toList();
    }

    // 메세지 보내기
    @Transactional
    public ChatMessageDTO sendMessage(Long roomId, Long senderId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지를 입력해주세요.");
        }

        ChatRoom chatRoom = getAuthorizedRoom(roomId, senderId);

        if (chatRoom.getRoomStatus() == RoomStatus.FINISHED) {
            throw new IllegalArgumentException("거래 완료된 채팅방");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content.trim())
                .readYn("N")
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        return ChatMessageDTO.builder()
                .messageId(savedMessage.getMessageId())
                .roomId(chatRoom.getRoomId())
                .senderId(sender.getUserId())
                .senderNickname(sender.getNickname())
                .content(savedMessage.getContent())
                .readYn(savedMessage.getReadYn())
                .sentAt(savedMessage.getSentAt())
                .build();
    }

    // 채팅방 나가기(시간 저장)
    @Transactional
    public void leaveRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = getAuthorizedRoom(roomId, userId);

        if (chatRoom.getBuyer().getUserId().equals(userId)) {
            chatRoom.setBuyerLeftAt(LocalDateTime.now());
        } else {
            chatRoom.setSellerLeftAt(LocalDateTime.now());
        }
    }

    // 판매 완료 후 7일이 지남 + 구매자, 판매자 모두 나간 채팅방 DB에서 삭제
    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredChatRooms() {
        List<ChatRoom> leftRooms = chatRoomRepository.findByBuyerLeftAtIsNotNullAndSellerLeftAtIsNotNull();

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        for (ChatRoom chatRoom : leftRooms) {
            Product product = chatRoom.getProduct();

            boolean isSold = product.getTradeStatus() == TradeStatus.SOLD;
            boolean isExpired = product.getSoldAt() != null
                    && product.getSoldAt().isBefore(sevenDaysAgo);

            if (isSold && isExpired) {
                chatMessageRepository.deleteByChatRoom_RoomId(chatRoom.getRoomId());
                chatRoomRepository.delete(chatRoom);
            }
        }
    }

    // 전체 읽지 않은 메세지 수
    public long getUnreadCount(Long userId) {
        return getMyRooms(userId).stream()
                .mapToLong(ChatRoomDTO::getUnreadCount)
                .sum();
    }

    // 거래 상태 변경
    @Transactional
    public void changeTradeStatus(Long roomId, Long userId, int tradeStatusId) {
        ChatRoom chatRoom = getAuthorizedRoom(roomId, userId);

        boolean isSeller = chatRoom.getProduct().getUser().getUserId().equals(userId);

        if (!isSeller) {
            throw new AccessDeniedException("판매자만 거래 상태를 변경할 수 있습니다.");
        }

        TradeStatus tradeStatus = TradeStatus.fromId(tradeStatusId);

        Product product = chatRoom.getProduct();

        if (tradeStatus == TradeStatus.RESERVED) {
            Trade trade = tradeRepository.findByProduct_ProductId(product.getProductId())
                    .orElse(null);

            if (trade != null
                    && !trade.getBuyer().getUserId()
                            .equals(chatRoom.getBuyer().getUserId())) {
                throw new IllegalArgumentException("이미 다른 구매자와 거래 중인 상품입니다.");
            }

            if (trade == null) {
                tradeRepository.save(
                        Trade.builder()
                                .product(product)
                                .seller(product.getUser())
                                .buyer(chatRoom.getBuyer())
                                .tradeDate(LocalDateTime.now())
                                .build());
            }

            product.setTradeStatus(TradeStatus.RESERVED);
            product.setSoldAt(null);
            chatRoom.setRoomStatus(RoomStatus.ACTIVE);

            return;
        }

        if (tradeStatus == TradeStatus.SOLD) {
            Trade trade = tradeRepository.findByProduct_ProductId(product.getProductId())
                    .orElse(null);

            if (trade == null) {
                trade = tradeRepository.save(
                        Trade.builder()
                                .product(product)
                                .seller(product.getUser())
                                .buyer(chatRoom.getBuyer())
                                .tradeDate(LocalDateTime.now())
                                .completedAt(LocalDateTime.now())
                                .build());
            } else {
                if (!trade.getBuyer().getUserId()
                        .equals(chatRoom.getBuyer().getUserId())) {
                    throw new IllegalArgumentException("다른 구매자와 거래가 진행 중인 상품입니다.");
                }

                if (trade.getCompletedAt() == null) {
                    trade.setCompletedAt(LocalDateTime.now());
                }
            }

            product.setTradeStatus(TradeStatus.SOLD);
            product.setSoldAt(LocalDateTime.now());
            chatRoom.setRoomStatus(RoomStatus.FINISHED);

            return;
        }

        tradeRepository.findByProduct_ProductId(product.getProductId())
                .ifPresent(tradeRepository::delete);

        product.setTradeStatus(TradeStatus.ON_SALE);
        product.setSoldAt(null);
        chatRoom.setRoomStatus(RoomStatus.ACTIVE);
    }

    // 판매자/구매자 아니면 채팅방 접근권한 없음
    private ChatRoom getAuthorizedRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        boolean isBuyer = chatRoom.getBuyer().getUserId().equals(userId);
        boolean isSeller = chatRoom.getProduct().getUser().getUserId().equals(userId);

        if (!isBuyer && !isSeller) {
            throw new AccessDeniedException("채팅방 접근 권한 없음");
        }

        return chatRoom;
    }

    // 채팅방을 목록에 표시할지 확인
    private boolean canShowRoom(ChatRoom chatRoom, Long userId) {
        LocalDateTime leftAt;

        if (chatRoom.getBuyer().getUserId().equals(userId)) {
            leftAt = chatRoom.getBuyerLeftAt();
        } else {
            leftAt = chatRoom.getSellerLeftAt();
        }

        // 나간 적 없으면 목록에 표시
        if (leftAt == null) {
            return true;
        }

        // 나간 뒤 상대가 보낸 메세지가 있으면 다시 목록에 표시
        return chatMessageRepository.existsByChatRoom_RoomIdAndSender_UserIdNotAndSentAtAfter(chatRoom.getRoomId(),
                userId, leftAt);
    }

    // Entity -> toDTO
    private ChatRoomDTO toRoomDTO(ChatRoom chatRoom, Long loginUserId) {
        boolean isBuyer = chatRoom.getBuyer().getUserId().equals(loginUserId);
        boolean isSeller = chatRoom.getProduct().getUser().getUserId().equals(loginUserId);

        String opponentNickname;
        Long opponentUserId;

        if (isBuyer) {
            opponentNickname = chatRoom.getProduct().getUser().getNickname();
            opponentUserId = chatRoom.getProduct().getUser().getUserId();
        } else {
            opponentNickname = chatRoom.getBuyer().getNickname();
            opponentUserId = chatRoom.getBuyer().getUserId();
        }

        ChatMessage lastMessage = chatMessageRepository.findTopByChatRoom_RoomIdOrderBySentAtDesc(chatRoom.getRoomId())
                .orElse(null);

        long unreadCount = chatMessageRepository
                .countByChatRoom_RoomIdAndSender_UserIdNotAndReadYn(chatRoom.getRoomId(), loginUserId, "N");

        return ChatRoomDTO.builder()
                .roomId(chatRoom.getRoomId())
                .productId(chatRoom.getProduct().getProductId())
                .productTitle(chatRoom.getProduct().getTitle())
                .productName(chatRoom.getProduct().getName())
                .opponentNickname(opponentNickname)
                .opponentUserId(opponentUserId)
                .roomStatus(chatRoom.getRoomStatus())
                .createdAt(chatRoom.getCreatedAt())
                .lastMessage(lastMessage == null ? null : lastMessage.getContent())
                .lastMessageSentAt(lastMessage == null ? chatRoom.getCreatedAt() : lastMessage.getSentAt())
                .unreadCount(unreadCount)
                .seller(isSeller)
                .tradeStatusId(chatRoom.getProduct().getTradeStatus().getId())
                .build();
    }
}
