package com.rho.ims.service;

import com.rho.ims.model.Supplier;
import com.rho.ims.respository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository){
        this.supplierRepository = supplierRepository;
    }

    public Supplier createSupplier(){

    }

    public List<Supplier> getAllSupplier(){

    }

    public Supplier getSupplier(Long id){

    }

    public Supplier updateSupplier(){

    }

    public void deleteSupplier(){

    }




}
