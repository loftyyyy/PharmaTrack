package com.rho.ims.controller;

import com.rho.ims.dto.CustomerCreateDTO;
import com.rho.ims.dto.CustomerResponseDTO;
import com.rho.ims.dto.CustomerUpdateDTO;
import com.rho.ims.model.Customer;
import com.rho.ims.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerCreateDTO customerCreateDTO){
        Customer customer = customerService.saveCustomer(customerCreateDTO);

        return ResponseEntity.ok().body(new CustomerResponseDTO(customer));

    }

    @GetMapping
    public ResponseEntity<?> getAllCustomer(){
        List<CustomerResponseDTO> customers = customerService.getAll().stream().map(customer -> new CustomerResponseDTO(customer)).toList();

        return ResponseEntity.ok().body(customers);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable Long id){
        Customer customer = customerService.getCustomer(id);

        return ResponseEntity.ok().body(new CustomerResponseDTO(customer));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@Valid @RequestBody CustomerUpdateDTO customerUpdateDTO, @PathVariable Long id){
        Customer customer = customerService.updateCustomer(customerUpdateDTO, id);

        return ResponseEntity.ok().body(customer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id){
        customerService.deleteCustomer(id);

        return ResponseEntity.ok().body("Customer deleted successfully");

    }
}
