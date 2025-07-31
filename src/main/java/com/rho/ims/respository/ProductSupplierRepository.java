package com.rho.ims.respository;

import com.rho.ims.model.Product;
import com.rho.ims.model.ProductSupplier;
import com.rho.ims.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductSupplierRepository extends JpaRepository<ProductSupplier, Long> {

    boolean existsByProductIdAndSupplierId(Long productId, Long supplierId);

    Optional<ProductSupplier> findBySupplierIdAndSupplierProductCode(Long supplierId, String supplierProductCode);
}
