package com.rho.ims.dto;


import com.rho.ims.enums.DrugClassification;
import com.rho.ims.model.Category;
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
    private String manufacturer;
    private String dosageForm;
    private String strength;
    private Integer minimumStock;
    private Boolean active;
    private DrugClassification drugClassification;
    private CategoryResponseDTO category;
    private String barcode;
    private String sku;
    private Long createdBy;

    public ProductResponseDTO(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.brand = product.getBrand();
        this.description = product.getDescription();
        this.category = new CategoryResponseDTO(product.getCategory());
        this.barcode = product.getBarcode();
        this.manufacturer = product.getManufacturer();
        this.dosageForm = product.getDosageForm();
        this.strength = product.getStrength();
        this.active = product.getActive();
        this.minimumStock = product.getMinimumStock();
        this.drugClassification = product.getDrugClassification();
        this.sku = product.getSku();
        this.createdBy = product.getCreatedBy().getId();
    }

}
