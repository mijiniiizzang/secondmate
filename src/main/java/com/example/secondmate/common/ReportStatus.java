package com.example.secondmate.common;

import lombok.Getter;

@Getter
public enum ReportStatus {
    PENDING(1, "처리중"),
    ACCEPTED(2, "처리 완료"),
    REJECTED(3, "반려");

    private final int id;
    private final String statusName;

    private ReportStatus(int id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    public static ReportStatus fromId(int id) {
        for(ReportStatus status : ReportStatus.values()) {
            if(status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 상태 아이디 : " + id);
    }
}
