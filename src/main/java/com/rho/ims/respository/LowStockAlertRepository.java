package com.rho.ims.respository;

import com.rho.ims.model.LowStockAlert;
import com.rho.ims.model.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LowStockAlertRepository extends JpaRepository<LowStockAlert, Long> {

    boolean existsByProductBatchAndResolvedFalse(ProductBatch productBatch);

    boolean existsByProductBatch(ProductBatch productBatch);
}
