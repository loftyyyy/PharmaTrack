package com.rho.ims.service;

import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.model.SaleItem;
import com.rho.ims.respository.ProductBatchRepository;
import com.rho.ims.respository.ProductRepository;
import com.rho.ims.respository.SaleItemRepository;
import com.rho.ims.respository.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleItemService {
    private final SaleItemRepository saleItemRepository;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ProductBatchRepository productBatchRepository;

    public SaleItemService(SaleItemRepository saleItemRepository, SaleRepository saleRepository, ProductRepository productRepository, ProductBatchRepository productBatchRepository){
        this.saleItemRepository = saleItemRepository;
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.productBatchRepository = productBatchRepository;
    }

    public List<SaleItem> getAll(){
        return saleItemRepository.findAll();
    }

    public SaleItem getSaleItem(Long id){
        SaleItem saleItem = saleItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sale item not found"));
        return saleItem;
    }

}
