package com.example.secondmate.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.secondmate.common.ProcessReason;
import com.example.secondmate.common.ProductCategory;
import com.example.secondmate.common.ReportStatus;
import com.example.secondmate.common.TargetType;
import com.example.secondmate.common.TradeStatus;
import com.example.secondmate.common.UserStatus;
import com.example.secondmate.entity.Product;
import com.example.secondmate.entity.Report;
import com.example.secondmate.entity.User;
import com.example.secondmate.service.AdminService;
import com.example.secondmate.service.ReportService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;

    // 관리자 마이페이지 홈화면
@GetMapping("/home")
public String adminHome(Model model) {
    Page<Product> recentProducts = adminService.getRecentProducts();

    model.addAttribute("menu", "home");
    model.addAttribute("totalUserCount", adminService.getTotalUserCount());
    model.addAttribute("suspendedUserCount", adminService.getSuspendedUserCount());
    model.addAttribute("todayProductCount", adminService.getTodayProductCount());
    model.addAttribute("recentProducts", recentProducts.getContent());
    model.addAttribute("totalReportCount", adminService.getTotalReportCount());
    model.addAttribute("pendingReportCount", adminService.getPendingReportCount());
    model.addAttribute("acceptedReportCount", adminService.getAcceptedReportCount());

    return "admin/home";
}

    // 회원 관리
    @GetMapping("/users")
    public String adminUsers(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "userId") Pageable pageable, Model model) {

        Page<User> users = adminService.getUserList(status, keyword, pageable);

        model.addAttribute("menu", "users");
        model.addAttribute("users", users);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        return "admin/users";
    }

    // 상품 관리
    @GetMapping("/products")
    public String adminProducts(
            @RequestParam(required = false) TradeStatus tradeStatus,
            @RequestParam(required = false) List<ProductCategory> categories,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "productId") Pageable pageable, Model model) {

        Page<Product> products = adminService.getProductList(tradeStatus, categories, keyword, pageable);
        model.addAttribute("menu", "products");
        model.addAttribute("products", products);
        model.addAttribute("tradeStatus", tradeStatus);
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        return "admin/products";
    }

    // 관리자 상품 숨김 처리
    @PostMapping("/products/hide")
    public String hideProduct(@RequestParam Long productId, @RequestParam ProcessReason processReason,
            @RequestParam String hiddenReason) {

        adminService.hideProduct(productId, processReason, hiddenReason);
        return "redirect:/admin/products";
    }

    // 관리자 상품 숨김 해제
    @PostMapping("/products/show")
    public String showProduct(@RequestParam Long productId) {
        adminService.showProduct(productId);
        return "redirect:/admin/products";
    }

    // 관리자 상품 강제 삭제
    @PostMapping("/products/delete")
    public String deleteProduct(@RequestParam Long productId, @RequestParam ProcessReason processReason,
            @RequestParam String deleteReason) {
        adminService.deleteProduct(productId, processReason, deleteReason);
        return "redirect:/admin/products";
    }

    // // 댓글 숨김 처리
    // @PostMapping("/comments/hide")
    // public String hideComment(@RequestParam Long reportId,
    // @RequestParam Long commentId,
    // @RequestParam ProcessReason processReason,
    // @RequestParam String detailReason,
    // @RequestParam Long productId) {
    // reportService.changeReportStatus(reportId, ReportStatus.ACCEPTED);
    // adminService.hideComment(commentId, processReason, detailReason);

    // return "redirect:/product/detail?productId=" + productId;
    // }

    // // 댓글 숨김 해제
    // @PostMapping("/comments/show")
    // public String showComment(@RequestParam Long commentId,
    // @RequestParam Long productId) {
    // adminService.showComment(commentId);

    // return "redirect:/product/detail?productId=" + productId;
    // }

    // // 댓글 강제 삭제
    // @PostMapping("/comments/delete")
    // public String deleteComment(@RequestParam Long commentId,
    // @RequestParam ProcessReason processReason,
    // @RequestParam String detailReason) {
    // adminService.deleteComment(commentId, processReason, detailReason);

    // return "redirect:/admin/comments";
    // }

    // 신고 관리
    @GetMapping("/reports")
    public String adminReports(
            @RequestParam(required = false) ReportStatus reportStatus,
            @RequestParam(required = false) TargetType targetType,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "reportId") Pageable pageable,
            Model model) {

        Page<Report> reports = reportService.getAdminReportList(reportStatus, targetType, keyword, pageable);

        model.addAttribute("menu", "reports");
        model.addAttribute("reports", reports);
        model.addAttribute("reportStatus", reportStatus);
        model.addAttribute("targetType", targetType);
        model.addAttribute("keyword", keyword);
        return "admin/reports";
    }

    // 신고 처리
    @PostMapping("/reports/process")
    public String processReport(@RequestParam Long reportId,
            @RequestParam String action,
            @RequestParam(required = false) ProcessReason processReason,
            @RequestParam(required = false) String detailReason) {

        Report report = reportService.getReport(reportId);

        if ("REJECT".equals(action)) {
            reportService.changeReportStatus(reportId, ReportStatus.REJECTED);
            return "redirect:/admin/reports";
        }

        if ("ACCEPT".equals(action)) {
            reportService.changeReportStatus(reportId, ReportStatus.ACCEPTED);
            return "redirect:/admin/reports";
        }

        if ("HIDE".equals(action) || "DELETE".equals(action)) {
            if (processReason == null || detailReason == null || detailReason.trim().isEmpty()) {
                throw new IllegalArgumentException("처리 사유와 상세 사유를 입력해주세요.");
            }

            if (report.getTargetType() == TargetType.USER) {
                throw new IllegalArgumentException("회원 신고는 숨김 또는 삭제 처리할 수 없습니다.");
            }

            reportService.changeReportStatus(reportId, ReportStatus.ACCEPTED);

            if ("HIDE".equals(action)) {
                if (report.getTargetType() == TargetType.PRODUCT) {
                    adminService.hideProduct(report.getTargetId(), processReason, detailReason);
                }

                if (report.getTargetType() == TargetType.COMMENT) {
                    adminService.hideComment(report.getTargetId(), processReason, detailReason);
                }
            }

            if ("DELETE".equals(action)) {
                if (report.getTargetType() == TargetType.PRODUCT) {
                    adminService.deleteProduct(report.getTargetId(), processReason, detailReason);
                }

                if (report.getTargetType() == TargetType.COMMENT) {
                    adminService.deleteComment(report.getTargetId(), processReason, detailReason);
                }
            }

            return "redirect:/admin/reports";
        }

        throw new IllegalArgumentException("잘못된 신고 처리 방식입니다.");
    }
}
