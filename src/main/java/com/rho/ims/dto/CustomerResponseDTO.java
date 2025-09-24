package com.rho.ims.dto;

import com.rho.ims.model.Customer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class CustomerResponseDTO {

    private Long customerId;
    private String name;
    private String phoneNumber;
    private String email;
    private String addressStreetBarangay;
    private String addressCityMunicipality;
    private String addressProvince;
    private String addressPostalCode;
    private LocalDateTime createdAt;

    public CustomerResponseDTO(Customer customer){
        this.customerId = customer.getId();
        this.name = customer.getName();
        this.phoneNumber = customer.getPhoneNumber();
        this.email = customer.getEmail();
        this.addressStreetBarangay = customer.getAddressStreetBarangay();
        this.addressCityMunicipality = customer.getAddressCityMunicipality();
        this.addressProvince = customer.getAddressProvince();
        this.addressPostalCode = customer.getAddressPostalCode();
        this.createdAt = customer.getCreatedAt();
    }

}
