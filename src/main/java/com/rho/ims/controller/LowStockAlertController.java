package com.rho.ims.controller;

import com.rho.ims.service.LowStockAlertService;
import org.springframework.stereotype.Controller;

@Controller
public class LowStockAlertController {
    private final LowStockAlertService lowStockAlertService;

    public LowStockAlertController(LowStockAlertService lowStockAlertService){
        this.lowStockAlertService = lowStockAlertService;
    }

}
