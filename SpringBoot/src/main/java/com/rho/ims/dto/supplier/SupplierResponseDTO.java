package com.rho.ims.dto.supplier;

import com.rho.ims.model.Supplier;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SupplierResponseDTO {

    private Long supplierId;
    private String name;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String addressStreetBarangay;
    private String addressCityMunicipality;
    private String addressProvince;
    private String addressPostalCode;
    private LocalDateTime createdAt;

    public SupplierResponseDTO(Supplier supplier){
        this.supplierId = supplier.getId();
        this.name = supplier.getName();
        this.contactPerson = supplier.getContactPerson();
        this.phoneNumber = supplier.getPhoneNumber();
        this.email = supplier.getEmail();
        this.addressStreetBarangay = supplier.getAddressStreetBarangay();
        this.addressCityMunicipality = supplier.getAddressCityMunicipality();
        this.addressProvince = supplier.getAddressProvince();
        this.addressPostalCode = supplier.getAddressPostalCode();
        this.createdAt = supplier.getCreatedAt();
    }

}
