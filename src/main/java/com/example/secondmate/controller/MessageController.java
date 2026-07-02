package com.example.secondmate.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.secondmate.dto.NotificationDTO;
import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/messages")
public class MessageController {
    private final NotificationService notificationService;

    // 사용자 쪽지함 이동
    @GetMapping
    public String messages(@PageableDefault(size=10, sort="createdAt", direction=Sort.Direction.DESC) Pageable pageable,
                           @AuthenticationPrincipal AccountDetails accountDetails, Model model
    ) {
        Page<NotificationDTO> notifications = notificationService.getNotificationList(accountDetails.getUserId(), pageable);
        model.addAttribute("notifications", notifications);

        return "user/messages";
        
    }

    // 쪽지 읽음처리
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(@PathVariable Long notificationId, @AuthenticationPrincipal AccountDetails accountDetails) {
        notificationService.readNotification(notificationId, accountDetails.getUserId());

        return ResponseEntity.ok().build();
    }

    // 안 읽은 알림
    @GetMapping("/unread")
    public ResponseEntity<Boolean> hasUnreadNotification(@AuthenticationPrincipal AccountDetails accountDetails) {
        boolean hasUnreadNotification = notificationService.hasUnreadNotification(accountDetails.getUserId());

        return ResponseEntity.ok(hasUnreadNotification);
    }

    // 쪽지 삭제
    @PostMapping("/{notificationId}/delete")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId, @AuthenticationPrincipal AccountDetails accountDetails) {
        notificationService.deleteNotification(notificationId, accountDetails.getUserId());

        return ResponseEntity.ok().build();
    }
}
