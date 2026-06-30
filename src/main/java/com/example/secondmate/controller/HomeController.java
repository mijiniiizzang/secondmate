package com.example.secondmate.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.secondmate.common.ReportStatus;
import com.example.secondmate.entity.Report;
import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.ReportService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ReportService reportService;

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal AccountDetails accountDetails, Model model) {
        // 로그인한 사용자의 신고 횟수
        if(accountDetails != null) {
            long acceptedReportCount = reportService.getReportCount(accountDetails.getUserId(), ReportStatus.ACCEPTED);
            model.addAttribute("acceptedReportCount", acceptedReportCount);
        }
        return "home";
    }
}