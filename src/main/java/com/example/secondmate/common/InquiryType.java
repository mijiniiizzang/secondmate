package com.example.secondmate.common;

import lombok.Getter;

@Getter
public enum InquiryType {
    ACCOUNT("계정 문의"),
    PRODUCT("상품 문의"),
    REPORT("신고 문의"),
    SERVICE("서비스 이용 문의"),
    ETC("기타");

    private final String label;

    InquiryType(String label) {
        this.label = label;
    }
}
