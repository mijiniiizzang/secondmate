package com.example.secondmate.common;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE(1, "정상"),
    SUSPENDED(2, "정지");

    private final int id;
    private final String statusName;

    private UserStatus(int id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    public static UserStatus fromId(int id) {
        for(UserStatus status : UserStatus.values()) {
            if(status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 상태 아이디 : " + id);
    }
}
