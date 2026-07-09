package com.example.secondmate.entity;

import java.time.LocalDateTime;

import com.example.secondmate.common.UserRole;
import com.example.secondmate.common.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userId;
    @Column(nullable=false, unique=true)
    private String username;
    @Column(nullable=false)
    private String password;
    @Column(nullable=false)
    private String name;
    @Column(nullable=false, unique=true)
    private String nickname;
    @Column(nullable=false)
    private String phone;
    @Column(nullable=false)
    private String email;
    @Column(nullable=false)
    private String address;
    @Column(nullable=false)
    private Double latitude;
    @Column(nullable=false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private UserRole role;
    
    // 신고
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private UserStatus status;
    @Builder.Default
    @Column(nullable=false)
    private int suspensionCount = 0;
    private LocalDateTime suspendedUntil;

    // 거래 완료 후
    @Builder.Default
    @Column(nullable=false)
    private Integer mateScore = 70;
}
