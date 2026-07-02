package com.example.secondmate.controller;

import com.example.secondmate.service.ReportService;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.secondmate.common.ProductCategory;
import com.example.secondmate.dto.ProductDTO;
import com.example.secondmate.dto.UserUpdateDTO;
import com.example.secondmate.dto.UserUpdatePasswordDTO;
import com.example.secondmate.dto.WishlistDTO;
import com.example.secondmate.entity.Report;
import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.ProductService;
import com.example.secondmate.service.UserService;
import com.example.secondmate.service.WishlistService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final ReportService reportService;
    private final UserService userService;
    private final ProductService productService;
    private final WishlistService wishlistService;

    // 마이페이지 기본 화면
    @GetMapping
    public String mypage(@AuthenticationPrincipal AccountDetails accountDetails, Model model) {
        model.addAttribute("user", userService.getUser(accountDetails.getUserId()));

        return "user/mypage/mypage";
    }

    // 내 회원정보
    @GetMapping("/info")
    public String infoForm(@AuthenticationPrincipal AccountDetails accountDetails, Model model) {
        model.addAttribute("user", userService.getUser(accountDetails.getUserId()));
        model.addAttribute("menu", "info");
        return "user/mypage/info";
    }

    // 회원정보 수정 처리
    @PostMapping("/info")
    public ResponseEntity<Void> infoUpdate(@ModelAttribute UserUpdateDTO updateDTO,
            @AuthenticationPrincipal AccountDetails accountDetails) {
        userService.updateUser(accountDetails.getUserId(), updateDTO);

        return ResponseEntity.ok().build();
    }

    // 비밀번호 변경
    @PostMapping("/password/check")
    public ResponseEntity<Void> checkCurrentPassword(@RequestParam String currentPassword,
            @AuthenticationPrincipal AccountDetails accountDetails) {
        try {
            userService.checkCurrentPassword(accountDetails.getUserId(), currentPassword);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@ModelAttribute UserUpdatePasswordDTO updatePasswordDTO,
            @AuthenticationPrincipal AccountDetails accountDetails) {
        userService.updatePassword(accountDetails.getUserId(), updatePasswordDTO);
        return ResponseEntity.ok().build();
    }

    // 회원 탈퇴
    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestParam String password, @AuthenticationPrincipal AccountDetails accountDetails) {
        try {
            userService.deleteUser(accountDetails.getUserId(), password);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 내가 쓴 글
    @GetMapping("/products")
    public String myProducts(@AuthenticationPrincipal AccountDetails accountDetails, @PageableDefault(size=5, sort="regDate", direction=Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<ProductDTO> products = productService.getMyProducts(accountDetails.getUserId(), pageable);

        model.addAttribute("products", products);
        
        return "user/mypage/products";
    }

    // 찜 내역
    @GetMapping("/wishlists")
    public String wishlists(@AuthenticationPrincipal AccountDetails accountDetails, @RequestParam(required=false) List<ProductCategory> categories,
                            @PageableDefault(size=5, sort="regDate", direction=Sort.Direction.DESC) Pageable pageable,
                            Model model
    ) {
        Page<WishlistDTO> wishlists = wishlistService.getWishlistList(accountDetails.getUserId(), categories, pageable);

        model.addAttribute("wishlists", wishlists);
        model.addAttribute("selectedCategories", categories);
        return "user/mypage/wishlists";
    }

    // 신고 내역
    @GetMapping("/reports")
    public String reports(@RequestParam(defaultValue="sent") String type, @PageableDefault(size=10, sort="regDate", direction=Sort.Direction.DESC) Pageable pageable,
                          @AuthenticationPrincipal AccountDetails accountDetails, Model model
    ) {
        Long userId = accountDetails.getUserId();

        Page<Report> reports;

        if ("received".equals(type)) {
            reports = reportService.getMyReceivedReportList(userId, pageable);
        } else {
            type = "sent";
            reports = reportService.getMyReportList(userId, pageable);
        }

        model.addAttribute("menu", "reports");
        model.addAttribute("type", type);
        model.addAttribute("reports", reports);
        
        return "user/mypage/reports";
    }

    // 내 채팅방
    @GetMapping("/chats")
    public String chats() {
        return "user/chats";
    }

}
