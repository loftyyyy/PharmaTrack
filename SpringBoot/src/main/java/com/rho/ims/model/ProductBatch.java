package com.rho.ims.model;

import com.rho.ims.enums.BatchStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBatch {

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

    @Column(name = "selling_price_per_unit", precision = 10, scale = 2, nullable = false)
    private BigDecimal sellingPricePerUnit;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "manufacturing_date", nullable = false)
    private LocalDate manufacturingDate;

    @Column(length = 50)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "batch_status", nullable = false)
    private BatchStatus batchStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

}
