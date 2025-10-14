package com.rho.ims.respository;

import com.rho.ims.model.Product;
import com.rho.ims.model.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {
    boolean existsByBatchNumber(String batchNumber);

    boolean existsByProductIdAndBatchNumber(Long productId, String batchNumber);

    boolean existsByProductId(Long productId);

    ProductBatch getByProductIdAndBatchNumber(Long productId, String batchNumber);

    Optional<ProductBatch> findByBatchNumber(String batchNumber);

    Optional<ProductBatch> findByProductIdAndBatchNumberAndManufacturingDateAndExpiryDate(Long productId, String batchNumber, LocalDate manufacturingDate, LocalDate expiryDate);

    List<ProductBatch> findByProductIdAndBatchNumber(Long productId, String batchNumber);

    List<ProductBatch> findByProductId(Long id);

    List<ProductBatch> findByQuantityLessThan(Integer quantity);

    @Query(value = """
    SELECT pb.* 
    FROM product_batches pb
    INNER JOIN (
        SELECT product_id, MIN(expiry_date) AS earliest_expiry
        FROM product_batches
        GROUP BY product_id
    ) grouped 
    ON pb.product_id = grouped.product_id 
    AND pb.expiry_date = grouped.earliest_expiry
""", nativeQuery = true)
    List<ProductBatch> findEarliestBatchForEachProduct();
}
