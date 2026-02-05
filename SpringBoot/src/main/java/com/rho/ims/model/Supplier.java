package com.rho.ims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    private String phoneNumber;

    private String email;

    @Column(name = "address_street_barangay", length = 255)
    private String addressStreetBarangay;

    @Column(name = "address_city_municipality", length = 100)
    private String addressCityMunicipality;

    @Column(name = "address_province", length = 100)
    private String addressProvince;

    @Column(name = "address_postal_code", length = 20)
    private String addressPostalCode;

    @Column(name = "supplier_code", nullable = false, unique = true)
    private String supplierCode;

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
