package com.rho.ims.service;

import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.sale.SaleCreateDTO;
import com.rho.ims.dto.saleItem.SaleItemCreateDTO;
import com.rho.ims.dto.sale.SaleVoidDTO;
import com.rho.ims.enums.ChangeType;
import com.rho.ims.enums.SaleStatus;
import com.rho.ims.model.*;
import com.rho.ims.respository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductBatchRepository productBatchRepository;
    private final SaleItemRepository saleItemRepository;
    private final UserRepository userRepository;
    private final InventoryLogRepository inventoryLogRepository;

    public SaleService(SaleRepository saleRepository, CustomerRepository customerRepository, ProductRepository productRepository, ProductBatchRepository productBatchRepository, SaleItemRepository saleItemRepository, UserRepository userRepository, InventoryLogRepository inventoryLogRepository){
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.productBatchRepository = productBatchRepository;
        this.saleItemRepository = saleItemRepository;
        this.userRepository = userRepository;
        this.inventoryLogRepository = inventoryLogRepository;
    }

    @Transactional
    public Sale saveSale(SaleCreateDTO saleCreateDTO) {
        Sale sale = new Sale();

        if (saleCreateDTO.getCustomerId() != null) {
            Customer customer = customerRepository.findById(saleCreateDTO.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            sale.setCustomer(customer);
        } else {
            Customer walkIn = customerRepository.findByName("Walk-in");
            if (walkIn == null) {
                throw new ResourceNotFoundException("Walk-in customer not found");
            }
            sale.setCustomer(walkIn);
        }

        sale.setSaleDate(saleCreateDTO.getSaleDate());
        sale.setPaymentMethod(saleCreateDTO.getPaymentMethod());
        sale.setDiscountAmount(saleCreateDTO.getDiscountAmount());
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        sale.setCreatedBy(user);
        sale.setStatus(SaleStatus.PENDING);

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
            saleItem.setQuantity(item.getQuantity());

            BigDecimal subTotal = BigDecimal.valueOf(item.getQuantity()).multiply(item.getUnitPrice());
            saleItem.setSubTotal(subTotal);

            totalAmount = totalAmount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            items.add(saleItem);
        }

        sale.setSaleItems(items);
        sale.setTotalAmount(totalAmount.subtract(sale.getDiscountAmount()));


        // TAX 12%

        BigDecimal taxRate = new BigDecimal("0.12");
        BigDecimal taxAmount = sale.getTotalAmount().multiply(taxRate);
        BigDecimal grandTotal = sale.getTotalAmount().add(taxAmount);

        sale.setTaxAmount(taxAmount);
        sale.setGrandTotal(grandTotal);

        return saleRepository.save(sale);
    }

    public List<Sale> getAll() {
        return saleRepository.findAll();
    }

    public Sale getSale(Long id){
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        return sale;
    }

    @Transactional
    public Sale confirmSale(Long saleId){
        Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        for(SaleItem saleItem : sale.getSaleItems()){
            ProductBatch productBatch = productBatchRepository.findById(saleItem.getProductBatch().getId()).orElseThrow(() -> new ResourceNotFoundException("Product batch not found"));

            productBatch.setQuantity(productBatch.getQuantity() - saleItem.getQuantity());
            productBatchRepository.save(productBatch);

            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setProduct(saleItem.getProduct());
            inventoryLog.setProductBatch(productBatch);
            inventoryLog.setSale(sale);
            inventoryLog.setQuantityChanged(saleItem.getQuantity());
            inventoryLog.setChangeType(ChangeType.OUT);
            inventoryLog.setPurchase(null);
            inventoryLog.setAdjustmentReference("Sale-" + saleItem.getSale().getId());
            inventoryLog.setReason("Sale confirmed");
            User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            inventoryLog.setCreatedBy(user);
            inventoryLogRepository.save(inventoryLog);
        }

        sale.setStatus(SaleStatus.CONFIRMED);
        return saleRepository.save(sale);


    }

    @Transactional
    public Sale cancelSale(Long saleId){
        Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        if(sale.getStatus() != SaleStatus.PENDING){
            throw new IllegalStateException("Only pending sales can be cancelled");

        }

        sale.setStatus(SaleStatus.CANCELLED);
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        sale.setUpdatedBy(user);

        return saleRepository.save(sale);
    }

    @Transactional
    public Sale voidSale(Long saleId, SaleVoidDTO saleVoidDTO) {
        Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        if(sale.getStatus() != SaleStatus.CONFIRMED){
            throw new IllegalStateException("Only confirmed sales can be voided");
        }

        if(Boolean.TRUE.equals(sale.getIsVoided())){
            throw new IllegalStateException("Sale is already voided");
        }

        for(SaleItem saleItem : sale.getSaleItems()){

            ProductBatch productBatch = productBatchRepository.findById(saleItem.getProductBatch().getId()).orElseThrow(() -> new ResourceNotFoundException("Product batch not found"));

            productBatch.setQuantity(productBatch.getQuantity() + saleItem.getQuantity());
            productBatchRepository.save(productBatch);

            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setProduct(saleItem.getProduct());
            inventoryLog.setProductBatch(productBatch);
            inventoryLog.setSale(sale);
            inventoryLog.setQuantityChanged(saleItem.getQuantity());
            inventoryLog.setChangeType(ChangeType.IN);
            inventoryLog.setPurchase(null);
            inventoryLog.setReason("Sale voided: " + saleVoidDTO.getVoidReason());
            User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            inventoryLog.setCreatedBy(user);
            inventoryLogRepository.save(inventoryLog);
        }

        sale.setIsVoided(true);
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        sale.setVoidedBy(user);
        sale.setVoidReason(saleVoidDTO.getVoidReason());
        sale.setVoidedAt(LocalDateTime.now());

        return saleRepository.save(sale);
    }



}
