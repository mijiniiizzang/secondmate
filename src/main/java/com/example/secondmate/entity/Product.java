package com.example.secondmate.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.example.secondmate.common.ProductCategory;
import com.example.secondmate.common.TradeStatus;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
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
public class Product {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long productId;

    // 상품 판매자
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    @Column(nullable=false)
    private String title;
    @Column(nullable=false)
    private String name;
    @Column(nullable=false)
    private Long price;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ProductCategory category;
    @Column(nullable=false)
    private String content;

    // 지역 설정
    @Column(nullable=false)
    private String city;
    @Column(nullable=false)
    private String gu;
    @Column(nullable=false)
    private Double latitude;
    @Column(nullable=false)
    private Double longitude;

    // 판매 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TradeStatus tradeStatus;
    @Builder.Default
    @Column(nullable=false)
    private boolean hidden = false;

    @CreationTimestamp
    private LocalDateTime regDate;

    @ColumnDefault("0")
    private int commentCount;

    @Builder.Default
    @OneToMany(
        mappedBy="product",
        fetch=FetchType.LAZY,
        cascade=CascadeType.ALL,
        orphanRemoval=true
    )
    private List<ProductImage> imageList = new ArrayList<>();
}
