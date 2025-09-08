package com.rho.ims.controller;

import com.rho.ims.dto.SupplierCreateDTO;
import com.rho.ims.dto.SupplierResponseDTO;
import com.rho.ims.dto.SupplierUpdateDTO;
import com.rho.ims.model.Supplier;
import com.rho.ims.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService){
        this.supplierService = supplierService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSupplier(@Valid @RequestBody SupplierCreateDTO supplierCreateDTO){
        Supplier supplier = supplierService.saveSupplier(supplierCreateDTO);
        return ResponseEntity.ok().body(new SupplierResponseDTO(supplier));
    }

    @GetMapping
    public ResponseEntity<?> getAllSupplier(){
        List<SupplierResponseDTO> suppliers = supplierService.getAll().stream().map( supplier -> new SupplierResponseDTO(supplier)).toList();
        return ResponseEntity.ok().body(suppliers);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getSupplier(@PathVariable Long id){
        Supplier supplier = supplierService.getSupplier(id);
        return ResponseEntity.ok().body(new SupplierResponseDTO(supplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@Valid @RequestBody SupplierUpdateDTO supplierUpdateDTO, @PathVariable Long id){
        Supplier supplier = supplierService.updateSupplier(supplierUpdateDTO, id);
        return ResponseEntity.ok().body(new SupplierResponseDTO(supplier));
    }

}
