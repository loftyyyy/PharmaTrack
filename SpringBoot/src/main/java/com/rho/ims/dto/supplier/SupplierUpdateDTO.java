package com.rho.ims.dto.supplier;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SupplierUpdateDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    @Size(max = 100, message = "")
    private String contactPerson;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Email(message = "Invalid Email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 255, message = "Street address or Barangay must not exceed 255 characters")
    private String addressStreetBarangay;

    @Size(max = 100, message = "City or Municipality must not exceed 100 characters")
    private String addressCityMunicipality;

    @Size(max = 100, message = "Province must not exceed 100 characters")
    private String addressProvince;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String addressPostalCode;

}
