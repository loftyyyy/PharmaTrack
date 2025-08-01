package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.PurchaseCreateDTO;
import com.rho.ims.enums.PurchaseStatus;
import com.rho.ims.model.*;
import com.rho.ims.respository.RoleRepository;
import com.rho.ims.respository.SupplierRepository;
import com.rho.ims.respository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@WithMockUser(username = "user", roles = "Staff")
class PurchaseControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private SupplierRepository supplierRepository;
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

    @Nested
    class CreatePurchaseTest {
        Role role;
        User user;
        Supplier supplier;
        LocalDate purchaseDate;


        @BeforeEach
        void setup(){
            supplierRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();

            role = new Role();
            role.setName("admin");
            roleRepository.save(role);

            user = new User();
            user.setRole(role);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);

            supplier = new Supplier();
            supplier.setName("Mercury Drug");
            supplier.setContactPerson("James");
            supplier.setPhoneNumber("0923423");
            supplier.setEmail("md@gmail.com");
            supplier.setAddressState("Cotabato");
            supplier.setAddressCity("Gensan");
            supplier.setAddressZipCode("8000");
            supplier.setCreatedBy(user);
            supplier.setAddressStreet("Rizal Street");
            supplierRepository.save(supplier);

            purchaseDate = LocalDate.now();


        }

        @DisplayName("Should create a purchase")
        @Test
        void shouldReturnSuccessfulRequest_createPurchase() throws Exception {
            PurchaseCreateDTO purchaseCreateDTO = new PurchaseCreateDTO();
            purchaseCreateDTO.setSupplierId(supplier.getId());
            purchaseCreateDTO.setPurchaseDate(purchaseDate);
            purchaseCreateDTO.setPurchaseStatus(PurchaseStatus.PENDING);
            purchaseCreateDTO.setTotalAmount(BigDecimal.valueOf(3520));

            mockMvc.perform(post("/api/v1/purchase/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchaseCreateDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());
        }


    }

    @Nested
    class FetchPurchaseTest {
        @BeforeEach
        void setup(){

        }

    }

    @Nested
    class UpdatePurchaseTest {
        @BeforeEach
        void setup(){

        }

    }

    @Nested
    class DeletePurchaseTest {
        @BeforeEach
        void setup(){

        }

    }


}