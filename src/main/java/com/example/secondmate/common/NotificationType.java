package com.example.secondmate.common;

import lombok.Getter;

@Getter
public enum NotificationType {
    PRODUCT_HIDDEN,
    PRODUCT_DELETED,
    COMMENT_HIDDEN,
    COMMENT_DELETED,
    USER_SUSPENDED,
    USER_UNSUSPENDED,
    REPORT_ACCEPTED,
    REPORT_REJECTED,
    INQUIRY_ANSWERED
}
