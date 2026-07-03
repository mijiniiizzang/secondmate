package com.example.secondmate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// WebSocket으로 보낼 데이터용 DTO

@Getter
@Setter
@NoArgsConstructor
public class ChatSendDTO {
    private Long roomId;
    private String content;
}
