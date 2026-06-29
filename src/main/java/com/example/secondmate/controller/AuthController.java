package com.example.secondmate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.secondmate.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    // 로그인 화면
    @GetMapping("/login")
    public void loginForm() {

    }

    // 아이디 찾기
    @GetMapping("/find-username")
    public void findUsernameForm() {

    }

    @PostMapping("/find-username")
    public String findUsername(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email,
            Model model) {
        try {
            String username = userService.findUsername(name, phone, email);
            model.addAttribute("username", username);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "auth/find-username";
    }

    // 비밀번호 찾기
    @GetMapping("/find-password")
    public void findPasswordForm() {

    }

    // 비밀번호 변경 전 본인 확인
    @PostMapping("/find-password/check")
    public String checkPasswordUser(
            @RequestParam String username,
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email,
            Model model) {
        try {
            userService.verifyPasswordResetUser(username, name, phone, email);

            model.addAttribute("verified", true);
            model.addAttribute("username", username);
            model.addAttribute("name", name);
            model.addAttribute("phone", phone);
            model.addAttribute("email", email);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "auth/find-password";
    }

    // 비밀번호 변경
    @PostMapping("/find-password/reset")
    public String resetPassword(
            @RequestParam String username,
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("verified", true);
            model.addAttribute("username", username);
            model.addAttribute("name", name);
            model.addAttribute("phone", phone);
            model.addAttribute("email", email);
            model.addAttribute("errorMsg", "새 비밀번호가 일치하지 않습니다.");

            return "auth/find-password";
        }

        try {
            userService.resetPassword(username, name, phone, email, newPassword);

            return "redirect:/auth/login?resetPassword=true";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "auth/find-password";
        }
    }
}
