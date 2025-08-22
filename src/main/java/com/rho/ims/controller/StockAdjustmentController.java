package com.rho.ims.controller;

import com.rho.ims.service.StockAdjustmentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stockAdjustment")
public class StockAdjustmentController {
    private final StockAdjustmentService stockAdjustmentService;

    public StockAdjustmentController(StockAdjustmentService stockAdjustmentService){
        this.stockAdjustmentService = stockAdjustmentService;
    }




}
