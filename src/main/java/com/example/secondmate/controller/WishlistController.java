package com.example.secondmate.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.WishlistService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

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
