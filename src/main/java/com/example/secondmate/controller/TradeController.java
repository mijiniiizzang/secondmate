package com.example.secondmate.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.secondmate.dto.TradeDTO;
import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.TradeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class TradeController {

    private final TradeService tradeService;

    @GetMapping("/trades")
    public String trades(
        @AuthenticationPrincipal AccountDetails accountDetails,
        Model model
    ) {
        Long userId = accountDetails.getUserId();

        List<TradeDTO> purchaseTrades =
                tradeService.getPurchaseTrades(userId);

        List<TradeDTO> salesTrades =
                tradeService.getSalesTrades(userId);

        model.addAttribute("purchaseTrades", purchaseTrades);
        model.addAttribute("salesTrades", salesTrades);

        return "user/mypage/trades";
    }
}