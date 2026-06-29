package com.example.secondmate.common;

import lombok.Getter;

@Getter
public enum RoomStatus {
    ACTIVE(1, "진행중"),
    FINISHED(2, "거래 완료");

    private final int id;
    private final String statusName;

    private RoomStatus(int id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    public static RoomStatus fromId(int id) {
        for(RoomStatus status : RoomStatus.values()) {
            if(status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 상태 아이디 : " + id);
    }

}
