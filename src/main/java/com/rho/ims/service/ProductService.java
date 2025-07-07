package com.rho.ims.service;

import com.rho.ims.model.Product;
import com.rho.ims.respository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts(){
        List<Product> products =  productRepository.findAll();

        return products;
    }
}
