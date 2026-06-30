package com.example.secondmate.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final UserService userService;

    // 마이페이지 기본 화면
    @GetMapping
    public String mypage(@AuthenticationPrincipal AccountDetails accountDetails, Model model) {
        model.addAttribute("user", userService.getUser(accountDetails.getUserId()));

        return "user/mypage";
    }

    // 내 회원정보
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal AccountDetails accountDetails, Model model) {
        model.addAttribute("user", userService.getUser(accountDetails.getUserId()));

        return "user/profile";
    }

    // 내가 쓴 글
    @GetMapping("/products")
    public String myProducts() {
        return "user/my-products";
    }

    // 찜 내역
    @GetMapping("/wishlists")
    public String wishlists() {
        return "user/wishlists";
    }

    // 신고 내역
    @GetMapping("/reports")
    public String reports() {
        return "user/reports";
    }

    // 내 채팅방
    @GetMapping("/chats")
    public String chats() {
        return "user/chats";
    }

}
