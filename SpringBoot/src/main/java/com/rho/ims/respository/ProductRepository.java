package com.rho.ims.respository;

import com.rho.ims.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {


    boolean existsByBarcode(String barcode);

    Optional<Product> findByBarcode(String barcode);
    Optional<Product> findBySku(String sku);
}
