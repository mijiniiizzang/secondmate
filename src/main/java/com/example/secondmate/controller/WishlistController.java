package com.example.secondmate.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.secondmate.dto.WishlistDTO;
import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.WishlistService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;


    // 특정 회원의 찜 리스트
    @GetMapping("/list/{userId}")
    public String listWishlist(@PathVariable Long userId, Model model) {
        List<WishlistDTO> wishlists = wishlistService.getWishlistList(userId);
        model.addAttribute("wishlists", wishlists);
        return "wishlist/list";
    }

    // 찜 목록에서 삭제
    @PostMapping("/delete/{userId}/{productId}")
    public String deleteWishlist(@PathVariable Long userId, @PathVariable Long productId) {
        wishlistService.deleteWishlist(userId, productId);
        return "redirect:/wishlist/list/" + userId;
    }

    // 찜 있으면 삭제, 없으면 추가
    @PostMapping("/toggle/{productId}")
    @ResponseBody
    public Map<String, Object> toggleWishlist(
        @PathVariable Long productId, @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        boolean wished = wishlistService.toggleWishlist(accountDetails.getUserId(), productId);

        long wishlistCount = wishlistService.getWishlistCount(productId);

        return Map.of(
            "wished", wished,
            "wishlistCount", wishlistCount
        );
    }

}
