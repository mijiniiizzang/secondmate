package com.example.secondmate.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.secondmate.common.ProductCategory;
import com.example.secondmate.common.TradeStatus;
import com.example.secondmate.dto.ProductDTO;
import com.example.secondmate.dto.ProductImageDTO;
import com.example.secondmate.entity.User;
import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.ProductService;
import com.example.secondmate.service.UserService;
import com.example.secondmate.service.WishlistService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final WishlistService wishlistService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 게시글 목록 페이지
    @GetMapping("/list")
    public void listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) List<ProductCategory> categories,
            @RequestParam(required = false) Boolean availableOnly,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String gu,
            @PageableDefault(size = 20, sort = "productId", direction = Sort.Direction.DESC) Pageable pageable,
            Model model,
            @AuthenticationPrincipal AccountDetails accountDetails) {
        // 홈에서 카테고리 하나만 선택한 경우
        if (category != null && (categories == null || categories.isEmpty())) {
            categories = List.of(category);
        }

        // 빈 문자열은 검색 조건에서 제외
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }

        if (city != null && city.isBlank()) {
            city = null;
        }

        if (gu != null && gu.isBlank()) {
            gu = null;
        }

        // 거래 가능 체크 시 판매중 상품만 조회
        TradeStatus tradeStatus = Boolean.TRUE.equals(availableOnly)
                ? TradeStatus.ON_SALE
                : null;

        Page<ProductDTO> products = productService.search(
                keyword,
                categories,
                city,
                gu,
                tradeStatus,
                minPrice,
                maxPrice,
                pageable);

        // 로그인한 사람만 찜 여부 조회
        if (accountDetails != null) {
            for (ProductDTO product : products.getContent()) {
                boolean isWished = wishlistService.isWished(
                        accountDetails.getUserId(),
                        product.getProductId());

                product.setWished(isWished);
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("categories", categories);
        model.addAttribute("availableOnly", availableOnly);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("city", city);
        model.addAttribute("gu", gu);

        model.addAttribute(
                "selectedCategories",
                categories == null
                        ? Collections.emptySet()
                        : categories.stream()
                                .map(Enum::name)
                                .collect(Collectors.toSet()));
    }

    // 상품 등록
    @GetMapping("/register")
    public String registerForm(
            Model model,
            @AuthenticationPrincipal AccountDetails accountDetails) {
        User user = userService.getUser(accountDetails.getUserId());

        String[] addressParts = user.getAddress().split(" ", 2);

        String savedCity = addressParts[0];
        String savedGu = addressParts.length > 1 ? addressParts[1] : "";

        model.addAttribute("savedCity", savedCity);
        model.addAttribute("savedGu", savedGu);
        model.addAttribute("latitude", user.getLatitude());
        model.addAttribute("longitude", user.getLongitude());

        return "product/register";
    }

    // 게시글 등록 처리
    @PostMapping("/register")
    public String registerProduct(
            @ModelAttribute ProductDTO productDTO,
            List<MultipartFile> files,
            @AuthenticationPrincipal AccountDetails accountDetails) {

        List<ProductImageDTO> imageList = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String originalName = file.getOriginalFilename();
                String changedName = UUID.randomUUID().toString();

                File savedFile = new File(uploadDir + changedName);

                try {
                    file.transferTo(savedFile);
                } catch (IOException e) {
                    throw new RuntimeException("파일 업로드 실패");
                }

                ProductImageDTO imageDTO = ProductImageDTO.builder()
                        .imageRealName(originalName)
                        .imageChgName(changedName)
                        .imagePath(uploadDir)
                        .build();
                imageList.add(imageDTO);
            }
        }

        productDTO.setImageList(imageList);
        productService.saveProduct(productDTO, accountDetails.getUserId());
        return "redirect:/product/list";
    }

    // 상품 상세 페이지
    @GetMapping("/detail")
    public void detailProduct(Long productId, Model model, @AuthenticationPrincipal AccountDetails accountDetails) {
        ProductDTO product = productService.getProductById(productId);
        long wishlistCount = wishlistService.getWishlistCount(productId);
        boolean isWished = false;
        boolean isOwner = false;

        // 로그인한 경우에만 "내가 찜 했는지" 확인 + 글 작성자만 수정/삭제 가능
        if (accountDetails != null) {
            isWished = wishlistService.isWished(accountDetails.getUserId(), productId);

            isOwner = productService.isProductOwner(productId, accountDetails.getUserId());
        }

        model.addAttribute("product", product);
        model.addAttribute("wishlistCount", wishlistCount);
        model.addAttribute("isWished", isWished);
        model.addAttribute("isOwner", isOwner);
    }

    // 상품 수정 페이지
    @GetMapping("/edit")
    public String editForm(Long productId, Model model, @AuthenticationPrincipal AccountDetails accountDetails) {
        if (!productService.isProductOwner(productId, accountDetails.getUserId())) {
            return "redirect:/product/detail?productId=" + productId;
        }
        ProductDTO product = productService.getProductById(productId);
        model.addAttribute("product", product);
        return "product/edit";
    }

    @PostMapping("/edit")
    public String editProduct(
            Long productId,
            ProductDTO productDTO,
            List<MultipartFile> files,
            @AuthenticationPrincipal AccountDetails accountDetails) {

        if (!productService.isProductOwner(productId, accountDetails.getUserId())) {
            return "redirect:/product/detail?productId=" + productId;
        }

        List<ProductImageDTO> imageList = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String originalName = file.getOriginalFilename();
                String changedName = UUID.randomUUID().toString();

                File savedFile = new File(uploadDir + changedName);

                try {
                    file.transferTo(savedFile);
                } catch (IOException e) {
                    throw new RuntimeException("파일 업로드 실패");
                }

                ProductImageDTO imageDTO = ProductImageDTO.builder()
                        .imageRealName(originalName)
                        .imageChgName(changedName)
                        .imagePath(uploadDir)
                        .build();
                imageList.add(imageDTO);
            }
        }

        productDTO.setImageList(imageList);
        productService.updateProduct(productId, productDTO);
        return "redirect:/product/detail?productId=" + productId;
    }

    // 상품 삭제
    @PostMapping("/delete")
    public String deleteProduct(Long productId, @AuthenticationPrincipal AccountDetails accountDetails) {
        if (!productService.isProductOwner(productId, accountDetails.getUserId())) {
            return "redirect:/product/detail?productId=" + productId;
        }
        productService.deleteProduct(productId);
        return "redirect:/product/list";
    }

    // 거래 상태 변경
    @PostMapping("/status")
    @ResponseBody
    public int updateTradeStatus(
            @RequestParam Long productId,
            @RequestParam TradeStatus tradeStatus,
            @AuthenticationPrincipal AccountDetails accountDetails) {

        // 작성자 본인인지 확인
        if (!productService.isProductOwner(productId, accountDetails.getUserId())) {
            return 0;
        }

        productService.updateTradeStatus(productId, tradeStatus);

        return 1;
    }
}
