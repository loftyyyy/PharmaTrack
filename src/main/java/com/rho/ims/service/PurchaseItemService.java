package com.rho.ims.service;

import com.rho.ims.model.PurchaseItem;
import com.rho.ims.respository.PurchaseItemRepository;
import org.springframework.stereotype.Service;

@Service
 public class PurchaseItemService {
    private final PurchaseItemRepository purchaseItemRepository;

    public PurchaseItemService(PurchaseItemRepository purchaseItemRepository) {
        this.purchaseItemRepository = purchaseItemRepository;

    }
}
