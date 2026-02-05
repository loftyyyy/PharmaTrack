package com.rho.ims.respository;

import com.rho.ims.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsBySupplierCode(String supplierCode);

    Optional<Supplier> findByName(String name);
}
