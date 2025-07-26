package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.ProductSupplierCreateDTO;
import com.rho.ims.dto.ProductSupplierUpdateDTO;
import com.rho.ims.model.Product;
import com.rho.ims.model.ProductSupplier;
import com.rho.ims.model.Supplier;
import com.rho.ims.respository.ProductRepository;
import com.rho.ims.respository.ProductSupplierRepository;
import com.rho.ims.respository.SupplierRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

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

        boolean exists = productSupplierRepository.existsByProductIdAndSupplierId(productSupplierCreateDTO.getProductId(), productSupplierCreateDTO.getSupplierId());
        if(exists){
            throw new DuplicateCredentialException("product supplier", "exists");
        }

        ProductSupplier productSupplier = new ProductSupplier();
        productSupplier.setProduct(product);
        productSupplier.setSupplier(supplier);
        productSupplier.setPreferredSupplier(productSupplierCreateDTO.getPreferredSupplier());
        productSupplier.setSupplierProductCode(productSupplierCreateDTO.getSupplierProductCode());

        return productSupplierRepository.save(productSupplier);
    }

    public List<ProductSupplier> getAll(){
        return productSupplierRepository.findAll();

    }

    public ProductSupplier getProductSupplier(Long id){
        return productSupplierRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("product supplier", id.toString()));

    }

    public ProductSupplier updateProductSupplier(ProductSupplierUpdateDTO productSupplierUpdateDTO, Long id){
        ProductSupplier productSupplier = productSupplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product supplier", id.toString()));

        productSupplier.setPreferredSupplier(productSupplierUpdateDTO.getPreferredSupplier());
        productSupplier.setSupplierProductCode(productSupplierUpdateDTO.getSupplierProductCode());

        return productSupplierRepository.save(productSupplier);
    }

    public void deleteProductSupplier(Long id){
        ProductSupplier productSupplier = productSupplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product supplier", id.toString()));
        productSupplierRepository.delete(productSupplier);
    }




}
