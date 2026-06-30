package com.example.secondmate.common;

import lombok.Getter;

@Getter
public enum ReportType {
    // 상품 신고
    PRODUCT_FRAUD("사기 의심"),
    PRODUCT_PROHIBITED_ITEM("판매 금지 물품"),
    PRODUCT_FALSE_INFORMATION("허위 상품 정보"),

    // 댓글 신고
    COMMENT_ABUSE("욕설 / 비방"),
    COMMENT_SPAM("도배 / 광고"),
    COMMENT_FALSE_INFORMATION("허위 정보"),

    // 사용자 신고
    USER_FRAUD("사기 의심"),
    USER_HARASSMENT("괴롭힘 / 비방"),
    USER_IMPERSONATION("사칭"),

    OTHER("기타");

    private final String typeName;
    
    private ReportType(String typeName) {
        this.typeName = typeName;
    }

}
