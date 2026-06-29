package com.example.secondmate.common;

import lombok.Getter;

@Getter
public enum TargetType {
    PRODUCT(1, "상품"),
    COMMENT(2, "댓글");

    private final int id;
    private final String typeName;

    private TargetType(int id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public static TargetType fromId(int id) {
        for(TargetType type : TargetType.values()) {
            if(type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 타입 아이디 : "  + id);
    }
}
