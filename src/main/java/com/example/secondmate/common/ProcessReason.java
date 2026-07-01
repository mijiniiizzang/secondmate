package com.example.secondmate.common;

import lombok.Getter;

@Getter
public enum ProcessReason {
    POLICY_VIOLATION("운영정책 위반"),
    PROHIBITED_ITEM("금지 품목 등록"),
    FALSE_INFORMATION("허위/과장 정보"),
    ABUSIVE_LANGUAGE("욕설/비방"),
    COPYRIGHT_ISSUE("저작권/상표권 문제"),
    OTHER("기타");

    private final String reasonName;
    ProcessReason(String reasonName) {
        this.reasonName = reasonName;
    }

    public String getReasonName() {
        return reasonName;
    }
}
