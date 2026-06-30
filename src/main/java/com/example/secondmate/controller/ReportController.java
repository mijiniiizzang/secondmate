package com.example.secondmate.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.secondmate.common.ReportType;
import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.ReportService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;

    // 상품 신고
    @PostMapping("/product/{productId}")
    public String reportProduct(@PathVariable Long productId, @RequestParam ReportType reportType, @RequestParam String reason, @AuthenticationPrincipal AccountDetails accountDetails) {
        reportService.reportProduct(accountDetails.getUserId(), productId, reportType, reason);
        return "redirect:/product/detail/" + productId;
    }

    // 댓글 신고
    @PostMapping("/comment/{commentId}")
    public String reportComment(@PathVariable Long commentId, @RequestParam Long productId, @RequestParam ReportType reportType, @RequestParam String reason, @AuthenticationPrincipal AccountDetails accountDetails) {
        reportService.reportComment(accountDetails.getUserId(), commentId, reportType, reason);
        return "redirect:/product/detail/" + productId;
    }

    // 사용자 신고 접수
    @PostMapping("/user/{reportedUserId}")
    public String reportUser(@PathVariable Long reportedUserId, @RequestParam ReportType reportType, @RequestParam String reason, @AuthenticationPrincipal AccountDetails accountDetails) {
        reportService.reportUser(accountDetails.getUserId(), reportedUserId, reportType, reason);
        return "redirect:/home";
    }
}
