package com.rho.ims.respository;

import com.rho.ims.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {


    boolean existsByBarcode(String barcode);
}
