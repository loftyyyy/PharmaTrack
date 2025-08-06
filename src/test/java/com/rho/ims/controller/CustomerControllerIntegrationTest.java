package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.CustomerCreateDTO;
import com.rho.ims.dto.CustomerUpdateDTO;
import com.rho.ims.model.Customer;
import com.rho.ims.model.User;
import com.rho.ims.respository.CustomerRepository;
import com.rho.ims.respository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@WithMockUser(username = "user", roles = "Staff")
class CustomerControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;


    @Nested
    class CreateCustomerTest {
        Customer customer;

        @BeforeEach
        void setup() {
            customerRepository.deleteAll();

            customer = new Customer();
            customer.setName("Melinda");
            customer.setEmail("melinda@gmail.com");
            customer.setPhoneNumber("09282714224");
            customer.setAddressZipCode("9402");
            customer.setAddressStreet("Garcia");
            customer.setAddressState("Cotabato");
            customerRepository.save(customer);

        }

        @DisplayName("Should create customer")
        @Test
        void shouldReturnSuccessfulRequest_createCustomer() throws Exception {
            CustomerCreateDTO customerCreateDTO = new CustomerCreateDTO();
            customerCreateDTO.setName("James");
            customerCreateDTO.setEmail("James32@gmail.com");
            customerCreateDTO.setPhoneNumber("09234234");
            customerCreateDTO.setAddressCity("Davao City");
            customerCreateDTO.setAddressState("Davao Del Sur");
            customerCreateDTO.setAddressStreet("Matina");
            customerCreateDTO.setAddressZipCode("8000");

            mockMvc.perform(post("/api/v1/customer/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerCreateDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }

        @DisplayName("Should fail when en email already exist")
        @Test
        void shouldReturnConflictRequest_duplicateEmail() throws Exception {

            CustomerCreateDTO customerCreateDTO = new CustomerCreateDTO();
            customerCreateDTO.setName("James");
            customerCreateDTO.setEmail("melinda@gmail.com");
            customerCreateDTO.setPhoneNumber("09234234");
            customerCreateDTO.setAddressCity("Davao City");
            customerCreateDTO.setAddressState("Davao Del Sur");
            customerCreateDTO.setAddressStreet("Matina");
            customerCreateDTO.setAddressZipCode("8000");

            mockMvc.perform(post("/api/v1/customer/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerCreateDTO)))
                    .andExpect(status().isConflict())
                    .andDo(print());


        }

        @DisplayName("Should fail when there are missing fields")
        @Test
        void shouldReturnBadRequest_missingFields() throws Exception {
            CustomerCreateDTO customerCreateDTO = new CustomerCreateDTO();
            customerCreateDTO.setName(null);
            customerCreateDTO.setEmail(null);
            customerCreateDTO.setPhoneNumber("09234234");
            customerCreateDTO.setAddressCity("Davao City");
            customerCreateDTO.setAddressState("Davao Del Sur");
            customerCreateDTO.setAddressStreet("Matina");
            customerCreateDTO.setAddressZipCode("8000");

            mockMvc.perform(post("/api/v1/customer/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerCreateDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());


        }

    }

    @Nested
    class FetchCustomerTest {
        Customer customer;
        Customer customer2;

        @BeforeEach
        void setup() {
            customerRepository.deleteAll();

            customer = new Customer();
            customer.setName("Melinda");
            customer.setEmail("melinda@gmail.com");
            customer.setPhoneNumber("09282714224");
            customer.setAddressZipCode("9402");
            customer.setAddressStreet("Garcia");
            customer.setAddressState("Cotabato");

            customer2 = new Customer();
            customer2.setName("James");
            customer2.setEmail("james@gmail.com");
            customer2.setPhoneNumber("09282714224");
            customer2.setAddressZipCode("9402");
            customer2.setAddressStreet("Garcia");
            customer2.setAddressState("Cotabato");

            customerRepository.saveAll(List.of(customer, customer2));

        }

        @DisplayName("Should fetch all customers")
        @Test
        void shouldReturnSuccessfulRequest_fetchAllCustomer() throws Exception {
            mockMvc.perform(get("/api/v1/customer").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andDo(print());

        }

        @DisplayName("Should fetch a specific customer given id")
        @Test
        void shouldReturnSuccessfulRequest_fetchSpecificCustomer() throws Exception {
            Long customerId = customer.getId();

            mockMvc.perform(get("/api/v1/customer/" + customerId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.customerId").value(customerId))
                    .andDo(print());

        }

        @DisplayName("Should fail when id doesn't exist")
        @Test
        void shouldReturnInternalServerErrorRequest_invalidCustomerId() throws Exception {

            Long customerId = 99L;

            mockMvc.perform(get("/api/v1/customer/" + customerId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andDo(print());

        }
    }

    @Nested
    class UpdateCustomerTest {
        Customer customer;
        Customer customer2;

        @BeforeEach
        void setup() {
            customerRepository.deleteAll();

            customer = new Customer();
            customer.setName("Melinda");
            customer.setEmail("melinda@gmail.com");
            customer.setPhoneNumber("09282714224");
            customer.setAddressZipCode("9402");
            customer.setAddressStreet("Garcia");
            customer.setAddressState("Cotabato");

            customer2 = new Customer();
            customer2.setName("James");
            customer2.setEmail("james@gmail.com");
            customer2.setPhoneNumber("09282714224");
            customer2.setAddressZipCode("9402");
            customer2.setAddressStreet("Garcia");
            customer2.setAddressState("Cotabato");

            customerRepository.saveAll(List.of(customer, customer2));

        }

        @DisplayName("Should update the customer")
        @Test
        void shouldReturnSuccessfulRequest_updateCustomer() throws Exception {
            Long customerId = customer.getId();

            CustomerUpdateDTO customerUpdateDTO = new CustomerUpdateDTO();
            customerUpdateDTO.setName("Melinda");
            customerUpdateDTO.setEmail("melinda30@gmail.com");
            customerUpdateDTO.setPhoneNumber("09282714224");
            customerUpdateDTO.setAddressZipCode("900");
            customerUpdateDTO.setAddressStreet("Rivas");
            customerUpdateDTO.setAddressState("Cotabato");

            mockMvc.perform(post("/api/v1/customer/" + customerId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerUpdateDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }

        @DisplayName("Should fail when updating a pre-existing email")
        @Test
        void shouldReturnConflictRequest_duplicateEmail() throws Exception {
            Long customerId = customer.getId();

            CustomerUpdateDTO customerUpdateDTO = new CustomerUpdateDTO();
            customerUpdateDTO.setName("Melinda");
            customerUpdateDTO.setEmail("james@gmail.com");
            customerUpdateDTO.setPhoneNumber("09282714224");
            customerUpdateDTO.setAddressZipCode("900");
            customerUpdateDTO.setAddressStreet("Rivas");
            customerUpdateDTO.setAddressState("Cotabato");

            mockMvc.perform(post("/api/v1/customer/" + customerId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerUpdateDTO)))
                    .andExpect(status().isConflict())
                    .andDo(print());

        }

        @DisplayName("Should fail when there are missing fields")
        @Test
        void shouldReturnBadRequest_missingFields() throws Exception {
            Long customerId = customer.getId();

            CustomerUpdateDTO customerUpdateDTO = new CustomerUpdateDTO();
            customerUpdateDTO.setName(null);
            customerUpdateDTO.setEmail("melinda30@gmail.com");
            customerUpdateDTO.setPhoneNumber("09282714224");
            customerUpdateDTO.setAddressZipCode("900");
            customerUpdateDTO.setAddressStreet("Rivas");
            customerUpdateDTO.setAddressState("Cotabato");

            mockMvc.perform(post("/api/v1/customer/" + customerId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerUpdateDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());

        }

    }

    @Nested
    class DeleteCustomerTest {
        Customer customer;
        Customer customer2;

        @BeforeEach
        void setup() {
            customerRepository.deleteAll();

            customer = new Customer();
            customer.setName("Melinda");
            customer.setEmail("melinda@gmail.com");
            customer.setPhoneNumber("09282714224");
            customer.setAddressZipCode("9402");
            customer.setAddressStreet("Garcia");
            customer.setAddressState("Cotabato");

            customer2 = new Customer();
            customer2.setName("James");
            customer2.setEmail("james@gmail.com");
            customer2.setPhoneNumber("09282714224");
            customer2.setAddressZipCode("9402");
            customer2.setAddressStreet("Garcia");
            customer2.setAddressState("Cotabato");

            customerRepository.saveAll(List.of(customer, customer2));

        }

        @DisplayName("Should delete the customer")
        @Test
        void shouldReturnSuccessfulRequest_deleteCustomer() throws Exception {
            Long customerId = customer.getId();

            mockMvc.perform(delete("/api/v1/customer/" + customerId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }

        @DisplayName("Should fail when customer is invalid")
        @Test
        void shouldReturnNotFoundRequest_invalidCustomerId() throws Exception {
            Long nonExistentCustomerId = 99L;

            mockMvc.perform(delete("/api/v1/customer/" + nonExistentCustomerId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());


        }

    }


}