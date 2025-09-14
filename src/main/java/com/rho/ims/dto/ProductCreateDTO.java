package com.rho.ims.dto;

import com.rho.ims.enums.DrugClassification;
import com.rho.ims.model.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductCreateDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 150)
    private String name;

    @NotBlank(message = "Brand is required")
    @Size(max = 100)
    private String brand;

    @NotBlank(message = "Manufacturer is required")
    @Size(max = 100)
    private String manufacturer;

    @NotBlank(message = "Manufacturer is required")
    @Size(max = 50)
    private String dosageForm;

    @NotBlank(message = "Manufacturer is required")
    @Size(max = 50)
    private String strength;

    @NotNull(message = "Minimum stock is required")
    @Min(0)
    private Integer minimumStock;

    @NotNull(message = "Drug classification is required")
    private DrugClassification drugClassification;

    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Barcode is required")
    @Size(max = 50)
    private String barcode;

}
