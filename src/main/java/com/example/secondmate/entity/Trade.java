package com.example.secondmate.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Trade {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long tradeId;
    
    @ManyToOne
    @JoinColumn(name="product_id", nullable=false, unique=true)
    private Product product;

    @ManyToOne
    @JoinColumn(name="seller_id", nullable=false)
    private User seller;

    @ManyToOne
    @JoinColumn(name="buyer_id", nullable=false)
    private User buyer;

    private LocalDateTime tradeDate;
    private LocalDateTime completedAt;
}
