package com.rho.ims.respository;

import com.rho.ims.model.LowStockAlert;
import com.rho.ims.model.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LowStockAlertRepository extends JpaRepository<LowStockAlert, Long> {

    boolean existsByProductBatchAndResolvedFalse(ProductBatch productBatch);

    boolean existsByProductBatch(ProductBatch productBatch);

    Optional<LowStockAlert> findByProductBatch(ProductBatch productBatch);

    List<LowStockAlert> findAllByResolved(Boolean resolved);

    Integer countByResolved(Boolean resolved);
}
