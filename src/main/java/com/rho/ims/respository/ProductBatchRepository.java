package com.rho.ims.respository;

import com.rho.ims.model.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductBatchRepository extends JpaRepository<Long, ProductBatch> {

}
