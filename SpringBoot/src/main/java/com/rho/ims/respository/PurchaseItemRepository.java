package com.rho.ims.respository;

import com.rho.ims.model.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    Optional<PurchaseItem> findByPurchaseIdAndProductBatchId(Long purchaseId, Long productBatchId);


}
