package com.example.secondmate.entity;

import java.time.LocalDateTime;

import com.example.secondmate.common.ReviewType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
public class Review {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id", nullable=false)
    private Product product;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="reviewer_id", nullable=false)
    private User reviewer;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="reviewee_id", nullable=false)
    private User reviewee;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="chat_room_id", nullable=false)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ReviewType reviewType;

    @Column(length=500)
    private String content;

    @Column(nullable=false)
    private LocalDateTime regDate;

    @PrePersist
    public void PrePersist() {
        this.regDate = LocalDateTime.now();
    }
}
