package com.example.secondmate.common;

import lombok.Getter;

@Getter
public enum TradeStatus {
    ON_SALE(1, "판매중"),
    RESERVED(2, "예약중"),
    SOLD(3, "판매완료");

    private final int id;
    private final String statusName;

    private TradeStatus(int id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    public static TradeStatus fromId(int id) {
        for(TradeStatus status : TradeStatus.values()) {
            if(status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 상태 아이디 : " + id);
    }
}