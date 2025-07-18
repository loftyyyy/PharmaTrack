package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.dto.ProductCreateDTO;
import com.rho.ims.dto.ProductUpdateDTO;
import com.rho.ims.model.Product;
import com.rho.ims.respository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Product saveProduct(ProductCreateDTO productCreateDTO){
        if (productRepository.existsByBarcode((productCreateDTO.getBarcode()))){
            throw new DuplicateCredentialException("barcode", productCreateDTO.getBarcode());
        }

        Product product = new Product();
        product.setName(productCreateDTO.getName());
        product.setBrand(productCreateDTO.getBrand());
        product.setDescription(productCreateDTO.getDescription());
        product.setCategoryId(product.getCategoryId());
        product.setBarcode(product.getBarcode());


        return productRepository.save(product);


    }

    public List<Product> getAll(){
        return productRepository.findAll();
    }

    public Product getProduct(Long id){
        return productRepository.findById(id).orElseThrow( () -> new RuntimeException("Product doesn't exist"));

    }

    public Product updateProduct(Long id, ProductUpdateDTO productUpdateDTO){
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product Doesn't Exist"));
        Optional<Product>  existing = productRepository.findByBarcode(productUpdateDTO.getBarcode());
        if(existing.isPresent() && !existing.getId().isEquals(id)){
            throw new DuplicateCredentialException("barcode", productUpdateDTO.getBarcode());
        }








    }
}
