package com.rho.ims.controller;

import com.rho.ims.service.PurchaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/purchaseItem")
public class PurchaseItemController {
    private final PurchaseService purchaseService;

    public PurchaseItemController(PurchaseService purchaseService){
        this.purchaseService = purchaseService;
    }


}
