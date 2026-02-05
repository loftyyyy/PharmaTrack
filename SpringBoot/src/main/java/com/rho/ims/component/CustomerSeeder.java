package com.rho.ims.component;

import com.rho.ims.model.Customer;
import com.rho.ims.respository.CustomerRepository;
import com.rho.ims.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CustomerSeeder implements CommandLineRunner {
    private final CustomerRepository customerRepository;

    public CustomerSeeder(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }
    @Override
    public void run(String... args) throws Exception {
        if(!customerRepository.existsByName("Walk-In")){
            Customer walkInCustomer = new Customer();
            walkInCustomer.setIsActive(Boolean.TRUE);
            walkInCustomer.setName("Walk-In");
            walkInCustomer.setEmail(null);
            walkInCustomer.setAddressPostalCode(null);
            walkInCustomer.setAddressProvince(null);
            walkInCustomer.setAddressCityMunicipality(null);
            walkInCustomer.setAddressStreetBarangay(null);
            customerRepository.save(walkInCustomer);

        }


    }
}
