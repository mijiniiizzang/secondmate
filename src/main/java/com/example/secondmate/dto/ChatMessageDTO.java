package com.example.secondmate.dto;

import java.time.LocalDateTime;

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
public class ChatMessageDTO {
    private Long messageId;

    private Long roomId;
    
    private Long senderId;
    private String senderNickname;

    private String content;
    private String readYn;

    private LocalDateTime sentAt;
    private boolean mine;
    
}
