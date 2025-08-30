package com.rho.ims.respository;

import com.rho.ims.model.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {
    boolean existsByBatchNumber(String batchNumber);

    boolean existsByProductIdAndBatchNumber(Long productId, String batchNumber);

    Optional<ProductBatch> findByBatchNumber(String batchNumber);

    List<ProductBatch> findByProductIdAndBatchNumber(Long productId, String batchNumber);
}
