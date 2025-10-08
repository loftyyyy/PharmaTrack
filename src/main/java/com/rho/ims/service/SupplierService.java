package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.supplier.SupplierCreateDTO;
import com.rho.ims.dto.supplier.SupplierUpdateDTO;
import com.rho.ims.model.Supplier;
import com.rho.ims.model.User;
import com.rho.ims.respository.SupplierRepository;
import com.rho.ims.respository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    public SupplierService(SupplierRepository supplierRepository, UserRepository userRepository){
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
    }

    public Supplier saveSupplier(SupplierCreateDTO supplierCreateDTO) {
        String normalizedName = supplierCreateDTO.getName().trim();

        if (supplierRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new DuplicateCredentialException("name", normalizedName);
        }

        String supplierCode = generateSupplierCode(normalizedName);

        if (supplierRepository.existsBySupplierCode(supplierCode)) {
            throw new DuplicateCredentialException("supplierCode", supplierCode);
        }

        Supplier supplier = new Supplier();
        supplier.setName(normalizedName);
        supplier.setEmail(supplierCreateDTO.getEmail());
        supplier.setContactPerson(supplierCreateDTO.getContactPerson());
        supplier.setPhoneNumber(supplierCreateDTO.getPhoneNumber());
        supplier.setAddressStreetBarangay(supplierCreateDTO.getAddressStreetBarangay());
        supplier.setAddressCityMunicipality(supplierCreateDTO.getAddressCityMunicipality());
        supplier.setAddressProvince(supplierCreateDTO.getAddressProvince());
        supplier.setAddressPostalCode(supplierCreateDTO.getAddressPostalCode());
        supplier.setSupplierCode(supplierCode);

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            throw new ResourceNotFoundException("User doesn't exist, make sure you are logged in");
        }
        supplier.setCreatedBy(user);

        return supplierRepository.save(supplier);
    }

    public List<Supplier> getAll(){
        return supplierRepository.findAll();
    }

    public Supplier getSupplier(Long id){
        return supplierRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("supplier", id.toString()));

    }

    public Supplier updateSupplier(SupplierUpdateDTO supplierUpdateDTO, Long id){
        Supplier supplier = supplierRepository.findById(id).orElseThrow( () -> new DuplicateCredentialException("supplier", id.toString()));
        Optional<Supplier> existing = supplierRepository.findByName(supplierUpdateDTO.getName());

        if(existing.isPresent() && !existing.get().getId().equals(id)){
            throw new DuplicateCredentialException("supplier", supplierUpdateDTO.getName());
        }

        supplier.setName(supplierUpdateDTO.getName());
        supplier.setPhoneNumber(supplierUpdateDTO.getPhoneNumber());
        supplier.setContactPerson(supplierUpdateDTO.getContactPerson());
        supplier.setEmail(supplierUpdateDTO.getEmail());
        supplier.setAddressStreetBarangay(supplierUpdateDTO.getAddressStreetBarangay());
        supplier.setAddressCityMunicipality(supplierUpdateDTO.getAddressCityMunicipality());
        supplier.setAddressProvince(supplierUpdateDTO.getAddressProvince());
        supplier.setAddressPostalCode(supplierUpdateDTO.getAddressPostalCode());
        supplier.setSupplierCode(generateSupplierCode(supplierUpdateDTO.getName()));

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        supplier.setUpdatedBy(user);

        return supplierRepository.save(supplier);



    }

    public static String generateSupplierCode(String supplierName) {
        String[] words = supplierName.split("\\s+");
        StringBuilder acronym = new StringBuilder();
        for (String word : words) {
            acronym.append(Character.toUpperCase(word.charAt(0)));
        }
        return acronym.toString();
    }

}
