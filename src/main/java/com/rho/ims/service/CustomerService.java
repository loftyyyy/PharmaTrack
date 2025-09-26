package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.CustomerCreateDTO;
import com.rho.ims.dto.CustomerUpdateDTO;
import com.rho.ims.model.Customer;
import com.rho.ims.respository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public Customer saveCustomer(CustomerCreateDTO customerCreateDTO) {

        if(customerRepository.existsByEmail(customerCreateDTO.getEmail())){
            throw new DuplicateCredentialException("Email already registered");
        }

        Customer customer = new Customer();
        customer.setName(customerCreateDTO.getName());
        customer.setEmail(customerCreateDTO.getEmail());
        customer.setPhoneNumber(customerCreateDTO.getPhoneNumber());
        customer.setAddressStreetBarangay(customer.getAddressStreetBarangay());
        customer.setAddressCityMunicipality(customerCreateDTO.getAddressCityMunicipality());
        customer.setIsActive(Boolean.TRUE);
        customer.setAddressProvince(customerCreateDTO.getAddressProvince());
        customer.setAddressPostalCode(customerCreateDTO.getAddressPostalCode());
        return customerRepository.save(customer);

    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Customer getCustomer(Long id){
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));

        return customer;
    }

    public Customer updateCustomer(CustomerUpdateDTO customerUpdateDTO, Long id){
        Optional<Customer> existing = customerRepository.findByEmail(customerUpdateDTO.getEmail());

        if(existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new DuplicateCredentialException("Email already exists");
        }

        Customer customer = new Customer();
        customer.setName(customerUpdateDTO.getName());
        customer.setEmail(customerUpdateDTO.getEmail());
        customer.setPhoneNumber(customerUpdateDTO.getPhoneNumber());
        customer.setAddressStreetBarangay(customer.getAddressStreetBarangay());
        customer.setAddressCityMunicipality(customerUpdateDTO.getAddressCityMunicipality());
        customer.setAddressProvince(customerUpdateDTO.getAddressProvince());
        customer.setAddressPostalCode(customerUpdateDTO.getAddressPostalCode());
        customer.setIsActive(customerUpdateDTO.getIsActive());
        return customerRepository.save(customer);
    }

    public List<Customer> getActiveCustomers(){
        List<Customer> activeCustomers = customerRepository.findByIsActive(Boolean.TRUE);
        return activeCustomers;
    }




}
