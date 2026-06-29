package com.example.secondmate.common;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_ADMIN(1, "관리자"),
    ROLE_USER(2, "회원");

    private final int id;
    private final String roleName;

    private UserRole(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    // id에 맞는 UserRole 객체 리턴
    public static UserRole fromId(int id) {
        for(UserRole role : UserRole.values()) {
            if(role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 아이디 : " + id);
    }
}
