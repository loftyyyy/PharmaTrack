package com.rho.ims.dto;


import com.rho.ims.enums.DrugClassification;
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
    private String categoryName;
    private String barcode;
    private Long createdBy;

    public ProductResponseDTO(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.brand = product.getBrand();
        this.description = product.getDescription();
        this.categoryName = product.getCategory().getName();
        this.barcode = product.getBarcode();
        this.manufacturer = product.getManufacturer();
        this.dosageForm = product.getDosageForm();
        this.strength = product.getStrength();
        this.active = product.getActive();
        this.minimumStock = product.getMinimumStock();
        this.drugClassification = product.getDrugClassification();
        this.createdBy = product.getCreatedBy().getId();
    }

}
