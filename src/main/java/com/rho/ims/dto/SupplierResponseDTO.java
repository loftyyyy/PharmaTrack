package com.rho.ims.dto;

import com.rho.ims.model.Supplier;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SupplierResponseDTO {
    private Long id;
    private String name;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressZipCode;

    public SupplierResponseDTO(Supplier supplier){
        this.id = supplier.getId();
        this.name = supplier.getName();
        this.contactPerson = supplier.getContactPerson();
        this.phoneNumber = supplier.getPhoneNumber();
        this.email = supplier.getEmail();
        this.addressStreet = supplier.getAddressStreet();
        this.addressCity = supplier.getAddressCity();
        this.addressState = supplier.getAddressState();
        this.addressZipCode = supplier.getAddressZipCode();
    }

}
