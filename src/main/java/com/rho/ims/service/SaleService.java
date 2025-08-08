package com.rho.ims.service;

import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.SaleCreateDTO;
import com.rho.ims.dto.SaleItemCreateDTO;
import com.rho.ims.dto.SaleUpdateDTO;
import com.rho.ims.model.*;
import com.rho.ims.respository.CustomerRepository;
import com.rho.ims.respository.ProductBatchRepository;
import com.rho.ims.respository.ProductRepository;
import com.rho.ims.respository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductBatchRepository productBatchRepository;

    public SaleService(SaleRepository saleRepository, CustomerRepository customerRepository, ProductRepository productRepository, ProductBatchRepository productBatchRepository){
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.productBatchRepository = productBatchRepository;
    }

    public Sale saveSale(SaleCreateDTO saleCreateDTO) {
        Sale sale = new Sale();

        if (saleCreateDTO.getCustomerId() != null) {
            Customer customer = customerRepository.findById(saleCreateDTO.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            sale.setCustomer(customer);
        } else {
            Customer walkIn = customerRepository.findByName("Walk-in Customer");
            if (walkIn == null) {
                throw new ResourceNotFoundException("Walk-in customer not found");
            }
            sale.setCustomer(walkIn);
        }

        sale.setSaleDate(saleCreateDTO.getSaleDate());
        sale.setPaymentMethod(saleCreateDTO.getPaymentMethod());
        sale.setDiscountAmount(saleCreateDTO.getDiscountAmount());

        saleRepository.save(sale);
        List<SaleItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(SaleItemCreateDTO item : saleCreateDTO.getItems()){
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            ProductBatch productBatch = productBatchRepository.findById(item.getProductBatchId()).orElseThrow(() -> new ResourceNotFoundException("Product batch not found"));

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setProduct(product);
            saleItem.setProductBatch(productBatch);
            saleItem.setUnitPrice(item.getUnitPrice());

            totalAmount = totalAmount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            items.add(saleItem);
        }

        sale.setSaleItems(items);
        sale.setTotalAmount(totalAmount.subtract(sale.getDiscountAmount()));

        //TODO: Implement Inventory Logs

        return saleRepository.save(sale);
    }

    public List<Sale> getAll() {
        return saleRepository.findAll();
    }

    public Sale getSale(Long id){
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        return sale;
    }




}
