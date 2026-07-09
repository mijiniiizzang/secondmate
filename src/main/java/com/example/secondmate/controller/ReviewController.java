package com.example.secondmate.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.secondmate.dto.ReviewCreateDTO;
import com.example.secondmate.service.ReviewService;
import com.example.secondmate.security.AccountDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/write")
    public String writeReview(ReviewCreateDTO reviewCreateDTO, @AuthenticationPrincipal AccountDetails accountDetails) {
        Long loginUserId = accountDetails.getUserId();

        reviewService.writeReview(loginUserId, reviewCreateDTO);
        return "redirect:/chat/list";
    }

    @GetMapping("/received")
    public String receivedReviews(@AuthenticationPrincipal AccountDetails accountDetails, Model model) {
        Long loginUserId = accountDetails.getUserId();

        model.addAttribute("reviewList", reviewService.getReceivedReviews(loginUserId));
        return "user/mypage/review-received";
    }

    @GetMapping("/written")
    public String writtenReviews(@AuthenticationPrincipal AccountDetails accountDetails, Model model) {
        Long loginUserId = accountDetails.getUserId();

        model.addAttribute("reviewList", reviewService.getWrittenReviews(loginUserId));
        return "user/mypage/review-written";
    }

    @GetMapping("/api/can-write")
    @ResponseBody
    public boolean canWriteReview(@RequestParam Long roomId, @AuthenticationPrincipal AccountDetails accountDetails) {
        return reviewService.canWriteReview(roomId, accountDetails.getUserId());
    }
}
