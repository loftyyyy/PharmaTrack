package com.rho.ims.controller;

import com.rho.ims.dto.stockAlert.LowStockAlertDTO;
import com.rho.ims.service.LowStockAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/alerts")
public class LowStockAlertController {
    private final LowStockAlertService lowStockAlertService;

    public LowStockAlertController(LowStockAlertService lowStockAlertService){
        this.lowStockAlertService = lowStockAlertService;
    }

    @GetMapping("/count")
    public ResponseEntity<?> getUnresolvedAlertCount(){
        Integer alertCount = lowStockAlertService.unresolvedAlertCount();
        return ResponseEntity.ok().body(alertCount);
    }

    @GetMapping("/unresolved")
    public ResponseEntity<?> getUnresolvedAlerts(){
        List<LowStockAlertDTO> unResolvedLowStockAlerts = lowStockAlertService.getUnresolvedAlerts().stream().map(unResolvedAlert -> new LowStockAlertDTO(unResolvedAlert)).toList();
        return ResponseEntity.ok().body(unResolvedLowStockAlerts);
    }

    @GetMapping("/resolved")
    public ResponseEntity<?> getResolvedAlerts(){
        List<LowStockAlertDTO> unResolvedLowStockAlerts = lowStockAlertService.getResolvedAlerts().stream().map(resolvedAlert -> new LowStockAlertDTO(resolvedAlert)).toList();
        return ResponseEntity.ok().body(unResolvedLowStockAlerts);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveAlert(@PathVariable Long id){
        lowStockAlertService.resolveAlert(id);
        return ResponseEntity.ok().body("Alert resvoled successfully");
    }

    @PostMapping("/createOrUpdate")
    public void createOrUpdateAlerts(){
        lowStockAlertService.updateOrCreateLowStockAlerts();
    }

}
