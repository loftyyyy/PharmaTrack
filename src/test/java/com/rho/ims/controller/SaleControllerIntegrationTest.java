package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.SaleCreateDTO;
import com.rho.ims.dto.SaleItemCreateDTO;
import com.rho.ims.enums.PaymentMethod;
import com.rho.ims.model.*;
import com.rho.ims.respository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@WithMockUser(username = "user", roles = "Staff")
class SaleControllerIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductBatchRepository productBatchRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Value("${test.user.username}")
    private String username;

    @Value("${test.user.email}")
    private String email;

    @Value("${test.user.password}")
    private String password;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class CreateSaleTest {
        Customer customer;
        ProductBatch productBatch;
        Product product;
        User user;
        Role role;
        Category category;
        LocalDate expiryDate;
        LocalDate manufactureDate;
        Sale sale;

        @BeforeEach
        void setup() {
            customerRepository.deleteAll();
            productBatchRepository.deleteAll();
            productRepository.deleteAll();
            categoryRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();
            saleRepository.deleteAll();




            role = new Role();
            role.setName("staff");
            roleRepository.save(role);

            user = new User();
            user.setRole(role);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);

            category = new Category();
            category.setName("Pain Relief");
            categoryRepository.save(category);

            product = new Product();
            product.setName("Paracetamol");
            product.setBrand("Generic");
            product.setCategory(category);
            product.setBarcode("xyz123");
            product.setCreatedBy(user);
            productRepository.save(product);


            expiryDate = LocalDate.of(2029,6,25);
            manufactureDate = LocalDate.of(2025, 8, 30);

            productBatch = new ProductBatch();
            productBatch.setProduct(product);
            productBatch.setBatchNumber("xy1234");
            productBatch.setQuantity(10);
            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(320));
            productBatch.setLocation("Japan");
            productBatch.setExpiryDate(expiryDate);
            productBatch.setManufacturingDate(manufactureDate);
            productBatch.setCreatedBy(user);
            productBatchRepository.save(productBatch);

            customer = new Customer();
            customer.setName("Melinda");
            customer.setEmail("melinda@gmail.com");
            customer.setPhoneNumber("09282714224");
            customer.setAddressZipCode("9402");
            customer.setAddressStreet("Garcia");
            customer.setAddressState("Cotabato");
            customerRepository.save(customer);


        }

        @DisplayName("Should create a sale")
        @Test
        void shouldReturnSuccessfulRequest_createSale() throws Exception {
            SaleItemCreateDTO saleItem = new SaleItemCreateDTO();
            saleItem.setProductBatchId(productBatch.getId());
            saleItem.setProductId(product.getId());
            saleItem.setQuantity(3);
            saleItem.setUnitPrice(BigDecimal.valueOf(35));


            SaleCreateDTO saleCreateDTO = new SaleCreateDTO();
            saleCreateDTO.setCustomerId(customer.getId());
            saleCreateDTO.setItems(List.of(saleItem));
            saleCreateDTO.setPaymentMethod(PaymentMethod.CASH);
            saleCreateDTO.setSaleDate(LocalDate.now());
            saleCreateDTO.setDiscountAmount(BigDecimal.ZERO);



            mockMvc.perform(post("/api/v1/sales/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(saleCreateDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }


    }

    @Nested
    class FetchSaleTest {

    }

    @Nested
    class ConfirmSaleTest {

    }

    @Nested
    class VoidSaleTest {

    }



}