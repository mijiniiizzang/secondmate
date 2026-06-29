package com.example.secondmate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.secondmate.dto.UserCreateDTO;
import com.example.secondmate.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입 폼
    @GetMapping("/register")
    public void registerForm() {}

    // 회원가입 처리
    @PostMapping("/register")
    public String register(@ModelAttribute UserCreateDTO userCreateDTO) {
        // 아이디가 중복이면
        if (userService.isDuplicateId(userCreateDTO.getUsername())) {
            return "user/register";
        }

        userService.register(userCreateDTO);
        return "redirect:/home";
    }

    // 아이디 중복 확인
    @GetMapping("/register/check-username")
    @ResponseBody
    public boolean checkId(@RequestParam String username) {
        return userService.isDuplicateId(username);
    }

    // 닉네임 중복 확인
    @GetMapping("/register/check-nickname")
    @ResponseBody
    public boolean checkNickname(@RequestParam String nickname) {
        return userService.isDuplicateNickname(nickname);
    }

}
