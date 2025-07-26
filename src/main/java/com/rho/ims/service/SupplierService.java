package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.SupplierCreateDTO;
import com.rho.ims.dto.SupplierUpdateDTO;
import com.rho.ims.model.Supplier;
import com.rho.ims.model.User;
import com.rho.ims.respository.SupplierRepository;
import com.rho.ims.respository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Security;
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

    public Supplier saveSupplier(SupplierCreateDTO supplierCreateDTO){
        if(supplierRepository.existsByName(supplierCreateDTO.getName())){
            throw new DuplicateCredentialException("name", supplierCreateDTO.getName());
        }

        Supplier supplier = new Supplier();
        supplier.setName(supplier.getName());
        supplier.setEmail(supplier.getEmail());
        supplier.setPhoneNumber(supplier.getPhoneNumber());
        supplier.setAddressCity(supplier.getAddressCity());
        supplier.setAddressState(supplier.getAddressState());
        supplier.setAddressStreet(supplier.getAddressStreet());
        supplier.setAddressZipCode(supplier.getAddressZipCode());
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
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
        supplier.setEmail(supplierUpdateDTO.getEmail());
        supplier.setAddressCity(supplierUpdateDTO.getAddressCity());
        supplier.setAddressZipCode(supplierUpdateDTO.getAddressZipCode());
        supplier.setAddressState(supplierUpdateDTO.getAddressState());
        supplier.setAddressStreet(supplierUpdateDTO.getAddressStreet());

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        supplier.setUpdatedBy(user);

        return supplierRepository.save(supplier);



    }

    public void deleteSupplier(Long id){
        Supplier supplier = supplierRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("supplier", id.toString()));
        supplierRepository.delete(supplier);

    }




}
