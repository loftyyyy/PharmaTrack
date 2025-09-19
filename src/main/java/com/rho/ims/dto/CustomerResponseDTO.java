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
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressZipCode;
    private LocalDateTime createdAt;

    public CustomerResponseDTO(Customer customer){
        this.customerId = customer.getId();
        this.name = customer.getName();
        this.phoneNumber = customer.getPhoneNumber();
        this.email = customer.getEmail();
        this.addressStreet = customer.getAddressStreet();
        this.addressCity = customer.getAddressCity();
        this.addressState = customer.getAddressState();
        this.addressZipCode = customer.getAddressZipCode();
        this.createdAt = customer.getCreatedAt();
    }

}
