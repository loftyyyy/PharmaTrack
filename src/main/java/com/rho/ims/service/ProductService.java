package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.ProductCreateDTO;
import com.rho.ims.dto.ProductUpdateDTO;
import com.rho.ims.enums.BatchStatus;
import com.rho.ims.model.Category;
import com.rho.ims.model.Product;
import com.rho.ims.model.ProductBatch;
import com.rho.ims.model.User;
import com.rho.ims.respository.CategoryRepository;
import com.rho.ims.respository.ProductBatchRepository;
import com.rho.ims.respository.ProductRepository;
import com.rho.ims.respository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductBatchRepository productBatchRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, UserRepository userRepository, ProductBatchRepository productBatchRepository){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.productBatchRepository = productBatchRepository;
    }

    public Product saveProduct(ProductCreateDTO productCreateDTO){
        if (productRepository.existsByBarcode((productCreateDTO.getBarcode()))){
            throw new DuplicateCredentialException("barcode", productCreateDTO.getBarcode());
        }

        String sku = generateSKU(productCreateDTO);
        Optional<Product> existingProductSku = productRepository.findBySku(sku);

        if(existingProductSku.isPresent() && existingProductSku.get().getBarcode().equals(productCreateDTO.getBarcode())){
            return existingProductSku.get();
        }


        Category category = categoryRepository.findById(productCreateDTO.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = new Product();
        product.setName(productCreateDTO.getName());
        product.setBrand(productCreateDTO.getBrand());
        product.setManufacturer(productCreateDTO.getManufacturer());
        product.setDosageForm(productCreateDTO.getDosageForm());
        product.setStrength(productCreateDTO.getStrength());
        product.setMinimumStock(productCreateDTO.getMinimumStock());
        product.setDrugClassification(productCreateDTO.getDrugClassification());
        product.setActive(Boolean.TRUE);
        product.setDescription(productCreateDTO.getDescription());
        product.setCategory(category);
        product.setBarcode(productCreateDTO.getBarcode());
        product.setSku(sku);
        product.setBatchManaged(Boolean.FALSE);

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        product.setCreatedBy(user);


        return productRepository.save(product);

    }

    public List<Product> getAll(){
        return productRepository.findAll();
    }

    public Product getProduct(Long id){
        return productRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("product","Product doesn't exist"));

    }

    public Product updateProduct(Long id, ProductUpdateDTO productUpdateDTO){
        Category category = categoryRepository.findById(productUpdateDTO.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("category","category not found"));
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product", "product not found"));
        Optional<Product>  existing = productRepository.findByBarcode(productUpdateDTO.getBarcode());

        if(existing.isPresent() && !existing.get().getId().equals(id)){
            throw new DuplicateCredentialException("barcode", productUpdateDTO.getBarcode());
        }

        if(!productUpdateDTO.getActive()){
            ProductBatch productBatch = productBatchRepository.findById(product.getId()).orElseThrow(() -> new ResourceNotFoundException("Product batch not found"));
            productBatch.setBatchStatus(BatchStatus.UNAVAILABLE);
        }

        product.setName(productUpdateDTO.getName());
        product.setBrand(productUpdateDTO.getBrand());
        product.setDescription(productUpdateDTO.getDescription());
        product.setBarcode(productUpdateDTO.getBarcode());
        product.setManufacturer(productUpdateDTO.getManufacturer());
        product.setDosageForm(productUpdateDTO.getDosageForm());
        product.setStrength(productUpdateDTO.getStrength());
        product.setMinimumStock(productUpdateDTO.getMinimumStock());
        product.setDrugClassification(productUpdateDTO.getDrugClassification());
        product.setActive(productUpdateDTO.getActive());
        product.setCategory(category);

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        product.setUpdatedBy(user);

        return productRepository.save(product);

    }


    private String generateSKU(ProductCreateDTO productCreateDTO){
      String nameAbbr = productCreateDTO.getName().replaceAll("[^A-Za-z]", "")
                .substring(0, Math.min(4, productCreateDTO.getName().length()))
                .toUpperCase();

        String strength = productCreateDTO.getStrength().replaceAll("\\s+", "").toUpperCase();

        String formAbbr = productCreateDTO.getDosageForm()
                .substring(0, Math.min(3, productCreateDTO.getDosageForm().length()))
                .toUpperCase();

        String brandAbbr = productCreateDTO.getBrand()
                .substring(0, Math.min(3, productCreateDTO.getBrand().length()))
                .toUpperCase();

        return String.format("%s-%s-%s-%s", nameAbbr, strength, formAbbr, brandAbbr);
    }

}
