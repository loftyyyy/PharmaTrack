package com.rho.ims.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import javax.naming.Name;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_batches")
public class ProductBatches {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "batch_number", nullable = false, length = 100)
    private String batchNumber;

    @Column(nullable = false)
    @Min(0)
    private Integer quantity;

    @Column(name = "purchase_price_per_unit", precision = 10, scale = 2, nullable = false)
    private BigDecimal purchasePricePerUnit;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "manufacturing_date", nullable = false)
    private LocalDateTime manufacturingDate;

    @Column(length = 50)
    private String location;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;



}
