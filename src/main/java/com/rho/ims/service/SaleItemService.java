package com.rho.ims.service;

import com.rho.ims.api.exception.InsufficientStockException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.SaleItemCreateDTO;
import com.rho.ims.dto.SaleItemResponseDTO;
import com.rho.ims.model.Product;
import com.rho.ims.model.ProductBatch;
import com.rho.ims.model.Sale;
import com.rho.ims.model.SaleItem;
import com.rho.ims.respository.ProductBatchRepository;
import com.rho.ims.respository.ProductRepository;
import com.rho.ims.respository.SaleItemRepository;
import com.rho.ims.respository.SaleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

//    @Transactional
//    public SaleItem saveSaleItem(SaleItemCreateDTO saleItemCreateDTO){
//        Sale sale = saleRepository.findById(saleItemCreateDTO.getSaleId()).orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
//        Product product = productRepository.findById(saleItemCreateDTO.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
//        ProductBatch productBatch = productBatchRepository.findById(saleItemCreateDTO.getProductBatchId()).orElseThrow(() -> new ResourceNotFoundException("Product batch not found"));
//        SaleItem saleItem = new SaleItem();
//        saleItem.setSale(sale);
//        saleItem.setProductBatch(productBatch);
//        saleItem.setProduct(product);
//        saleItem.setUnitPrice(saleItemCreateDTO.getUnitPrice());
//
//        if(productBatch.getQuantity() < saleItemCreateDTO.getQuantity()){
//            throw new InsufficientStockException("Not enough stock in this batch, " + productBatch.getId() + " for product " + product.getName());
//        }
//        saleItem.setQuantity(saleItemCreateDTO.getQuantity());
//
//        // Not sure for this one TODO: think about this. Only modify the quantity once the sale has been confirmed
//        productBatch.setQuantity(productBatch.getQuantity() - saleItemCreateDTO.getQuantity());
//        productBatchRepository.save(productBatch);
//
//        BigDecimal subTotal = saleItemCreateDTO.getUnitPrice().multiply(BigDecimal.valueOf(saleItemCreateDTO.getQuantity()));
//        saleItem.setSubTotal(subTotal);
//
//        return saleItemRepository.save(saleItem);
//
//    }
//
    public List<SaleItem> getAll(){
        return saleItemRepository.findAll();

    }

    public SaleItem getSaleItem(Long id){
        SaleItem saleItem = saleItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sale item not found"));
        return saleItem;
    }

}
