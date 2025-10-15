package com.rho.ims.controller;

import com.rho.ims.service.LowStockAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/alerts")
public class LowStockAlertController {
    private final LowStockAlertService lowStockAlertService;

    public LowStockAlertController(LowStockAlertService lowStockAlertService){
        this.lowStockAlertService = lowStockAlertService;
    }

    @GetMapping
    public void createOrUpdateAlerts(){
        lowStockAlertService.updateOrCreateLowStockAlerts();
    }

}
