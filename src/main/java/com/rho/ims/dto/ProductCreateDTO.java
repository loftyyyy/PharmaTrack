package com.rho.ims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductCreateDTO {

    @NotNull(message = "Name is required")
    @Size(max = 150)
    private String name;

    @NotBlank(message = "Brand is required")
    @Size(max = 100)
    private String brand;

    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Barcode is required")
    @Size(max = 50)
    private String barcode;











}
