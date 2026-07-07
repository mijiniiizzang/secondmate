package com.example.secondmate.dto;

import java.time.LocalDateTime;

import com.example.secondmate.common.RoomStatus;

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
public class ChatRoomDTO {
    private Long roomId;

    private Long productId;
    private String productTitle;
    private String productName;

    private String opponentNickname;
    private Long opponentUserId;

    private RoomStatus roomStatus;
    private int tradeStatusId;
    private LocalDateTime createdAt;

    private String lastMessage;
    private LocalDateTime lastMessageSentAt;
    private long unreadCount;

    private boolean seller;
}
