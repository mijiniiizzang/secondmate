package com.example.secondmate.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String messages(@PageableDefault(size=10, sort="createdAt") Pageable pageable,
                           @AuthenticationPrincipal AccountDetails accountDetails, Model model
    ) {
        Page<NotificationDTO> notifications = notificationService.getNotificationList(accountDetails.getUserId(), pageable);
        model.addAttribute("notifications", notifications);

        return "user/messages";
        
    }
}
