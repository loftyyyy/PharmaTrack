package com.rho.ims.dto;


import com.rho.ims.model.Product;
import com.rho.ims.respository.CategoryRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductResponseDTO {

    private Long id;;
    private String name;
    private String brand;
    private String description;
    private Long categoryId;
    private String barcode;
    private Long createdBy;

    public ProductResponseDTO(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.brand = product.getBrand();
        this.description = product.getDescription();
        this.categoryId = product.getCategory().getId();
        this.barcode = product.getBarcode();
        this.createdBy = product.getCreatedBy().getId();
    }

}
