package com.rho.ims.controller;

import com.rho.ims.dto.InventoryLogCreateDTO;
import com.rho.ims.dto.InventoryLogResponseDTO;
import com.rho.ims.model.InventoryLog;
import com.rho.ims.service.InventoryLogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventoryLogs")
public class InventoryLogController {
    private final InventoryLogService inventoryLogService;

    public InventoryLogController(InventoryLogService inventoryLogService){
        this.inventoryLogService = inventoryLogService;

    }
    
    @RequestMapping("/create")
    public ResponseEntity<?> createInventoryLog(@Valid @RequestBody InventoryLogCreateDTO inventoryLogCreateDTO){
        InventoryLog inventoryLog = inventoryLogService.saveInventoryLog(inventoryLogCreateDTO);


        return ResponseEntity.ok().body(new InventoryLogResponseDTO(inventoryLog));
    }

    @RequestMapping("/")
    public ResponseEntity<?> getAllInventoryLog() {
        List<InventoryLogResponseDTO> inventoryLogs = inventoryLogService.getAll().stream().map(inventoryLog -> new InventoryLogResponseDTO(inventoryLog)).toList();

        return ResponseEntity.ok().body(inventoryLogs);
    }

    @RequestMapping("/{id}")
    public ResponseEntity<?> getInventoryLog(@PathVariable Long id){
        InventoryLog inventoryLog = inventoryLogService.getInventoryLog(id);

        return ResponseEntity.ok().body(new InventoryLogResponseDTO(inventoryLog));

    }


    
}
