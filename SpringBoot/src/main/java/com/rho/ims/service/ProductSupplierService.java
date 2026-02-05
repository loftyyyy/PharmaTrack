package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.productSupplier.ProductSupplierCreateDTO;
import com.rho.ims.dto.productSupplier.ProductSupplierUpdateDTO;
import com.rho.ims.model.Product;
import com.rho.ims.model.ProductSupplier;
import com.rho.ims.model.Supplier;
import com.rho.ims.respository.ProductRepository;
import com.rho.ims.respository.ProductSupplierRepository;
import com.rho.ims.respository.SupplierRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductSupplierService {

    private final ProductSupplierRepository productSupplierRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductSupplierService(ProductSupplierRepository productSupplierRepository, ProductRepository productRepository, SupplierRepository supplierRepository){
        this.productSupplierRepository = productSupplierRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    public ProductSupplier saveProductSupplier(ProductSupplierCreateDTO productSupplierCreateDTO){
        Product product = productRepository.findById(productSupplierCreateDTO.getProductId()).orElseThrow( () -> new ResourceNotFoundException("product", productSupplierCreateDTO.getProductId().toString()));
        Supplier supplier = supplierRepository.findById(productSupplierCreateDTO.getSupplierId()).orElseThrow( () -> new ResourceNotFoundException("supplier", productSupplierCreateDTO.getSupplierId().toString()));

        Optional<ProductSupplier> exists = productSupplierRepository.findByProductIdAndSupplierId(productSupplierCreateDTO.getProductId(), productSupplierCreateDTO.getSupplierId());

        if(exists.isPresent()){
            throw new DuplicateCredentialException("Product Supplier relationship already exists");
        }

        Optional<ProductSupplier> existing = productSupplierRepository.findBySupplierIdAndSupplierProductCode(productSupplierCreateDTO.getSupplierId(), productSupplierCreateDTO.getSupplierProductCode());
        if(existing.isPresent()){
            throw new DuplicateCredentialException("Supplier product code already exists for this supplier");
        }



        ProductSupplier productSupplier = new ProductSupplier();
        productSupplier.setProduct(product);
        productSupplier.setSupplier(supplier);
        productSupplier.setPreferredSupplier(productSupplierCreateDTO.getPreferredSupplier());
        productSupplier.setSupplierProductCode(productSupplierCreateDTO.getSupplierProductCode());

        return productSupplierRepository.save(productSupplier);
    }

    public ProductSupplier findOrCreateProductSupplier(ProductSupplierCreateDTO dto){
        return productSupplierRepository
                .findByProductIdAndSupplierId(dto.getProductId(), dto.getSupplierId())
                .orElseGet(() -> {
                    try {
                        return saveProductSupplier(dto);
                    } catch (DuplicateCredentialException | DataIntegrityViolationException e) {
                        // Race condition: another thread created it
                        return productSupplierRepository
                                .findByProductIdAndSupplierId(dto.getProductId(), dto.getSupplierId())
                                .orElseThrow(() -> new IllegalStateException(
                                        "ProductSupplier creation failed and record not found"));
                    }
                });
    }

    public List<ProductSupplier> getAll(){
        return productSupplierRepository.findAll();

    }

    public ProductSupplier getProductSupplier(Long id){
        return productSupplierRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("product supplier", id.toString()));
    }

    public ProductSupplier updateProductSupplier(ProductSupplierUpdateDTO productSupplierUpdateDTO, Long id){
        ProductSupplier productSupplier = productSupplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product supplier", id.toString()));

        Optional<ProductSupplier> existing = productSupplierRepository.findBySupplierIdAndSupplierProductCode(productSupplier.getSupplier().getId(), productSupplierUpdateDTO.getSupplierProductCode());

        if(existing.isPresent() && !existing.get().getId().equals(id)){
            throw new DuplicateCredentialException("Supplier product code already exists for this supplier");
        }

        productSupplier.setPreferredSupplier(productSupplierUpdateDTO.getPreferredSupplier());
        productSupplier.setSupplierProductCode(productSupplierUpdateDTO.getSupplierProductCode());

        return productSupplierRepository.save(productSupplier);
    }

}
