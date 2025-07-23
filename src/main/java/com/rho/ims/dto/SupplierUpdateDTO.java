package com.rho.ims.dto;


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

    @Size(max = 255, message = "Street address must not exceed 255 characters")
    private String addressStreet;


    @Size(max = 100, message = "City address must not exceed 100 characters")
    private String addressCity;

    @Size(max = 100, message = "State address must not exceed 100 characters")
    private String addressState;

    @Size(max = 20, message = "Zip code must not exceed 20 characters")
    private String addressZipCode;


}
