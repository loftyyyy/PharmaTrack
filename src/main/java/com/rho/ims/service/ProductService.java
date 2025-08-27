package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.ProductCreateDTO;
import com.rho.ims.dto.ProductUpdateDTO;
import com.rho.ims.model.Category;
import com.rho.ims.model.Product;
import com.rho.ims.model.User;
import com.rho.ims.respository.CategoryRepository;
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

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, UserRepository userRepository){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public Product saveProduct(ProductCreateDTO productCreateDTO){
        if (productRepository.existsByBarcode((productCreateDTO.getBarcode()))){
            throw new DuplicateCredentialException("barcode", productCreateDTO.getBarcode());
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

        // TODO: Reimplement this <>
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

        //TODO: Reimplement this. Used for testing phase only
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        product.setUpdatedBy(user);

        return productRepository.save(product);

    }

    public void deleteProduct(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product", "product not found"));

        productRepository.delete(product);

    }
}
