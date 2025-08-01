package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.PurchaseCreateDTO;
import com.rho.ims.dto.PurchaseUpdateDTO;
import com.rho.ims.enums.PurchaseStatus;
import com.rho.ims.model.*;
import com.rho.ims.respository.PurchaseRepository;
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
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Nested
    class CreatePurchaseTest {
        Role role;
        User user;
        Supplier supplier;
        LocalDate purchaseDate;
        Purchase purchase;


        @BeforeEach
        void setup(){
            purchaseRepository.deleteAll();
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

            purchase = new Purchase();
            purchase.setSupplier(supplier);
            purchase.setPurchaseStatus(PurchaseStatus.PENDING);
            purchase.setTotalAmount(BigDecimal.valueOf(23423));
            purchase.setPurchaseDate(purchaseDate);
            purchase.setCreatedBy(user);


            purchaseRepository.save(purchase);

        }

        @DisplayName("Should create a purchase")
        @Test
        void shouldReturnSuccessfulRequest_createPurchase() throws Exception {
            PurchaseCreateDTO purchaseCreateDTO = new PurchaseCreateDTO();
            purchaseCreateDTO.setSupplierId(supplier.getId());
            purchaseCreateDTO.setPurchaseDate(purchaseDate);
            purchaseCreateDTO.setPurchaseStatus(PurchaseStatus.RECEIVED);
            purchaseCreateDTO.setTotalAmount(BigDecimal.valueOf(3520));

            mockMvc.perform(post("/api/v1/purchase/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchaseCreateDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());
        }

        @DisplayName("Should fail if the supplier has a pending purchase")
        @Test
        void shouldReturnConflictRequest_pendingPurchase() throws Exception {
            PurchaseCreateDTO purchaseCreateDTO = new PurchaseCreateDTO();
            purchaseCreateDTO.setSupplierId(supplier.getId());
            purchaseCreateDTO.setPurchaseDate(purchaseDate);
            purchaseCreateDTO.setPurchaseStatus(PurchaseStatus.PENDING);
            purchaseCreateDTO.setTotalAmount(BigDecimal.valueOf(3520));

            mockMvc.perform(post("/api/v1/purchase/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchaseCreateDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Supplier already has a pending purchase"))
                    .andDo(print());

        }

        @DisplayName("Should fail when there's missing field/s")
        @Test
        void shouldReturnBadRequest_missingFields() throws Exception {

            PurchaseCreateDTO purchaseCreateDTO = new PurchaseCreateDTO();
            purchaseCreateDTO.setSupplierId(null);
            purchaseCreateDTO.setPurchaseDate(null);
            purchaseCreateDTO.setPurchaseStatus(PurchaseStatus.PENDING);
            purchaseCreateDTO.setTotalAmount(null);

            mockMvc.perform(post("/api/v1/purchase/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchaseCreateDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.length()").value(3))
                    .andDo(print());
        }



    }

    @Nested
    class FetchPurchaseTest {
        Role role;
        User user;
        Supplier supplier;
        LocalDate purchaseDate;
        Purchase purchase;
        Purchase purchase2;



        @BeforeEach
        void setup(){
            purchaseRepository.deleteAll();
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

            purchase = new Purchase();
            purchase.setSupplier(supplier);
            purchase.setPurchaseStatus(PurchaseStatus.PENDING);
            purchase.setTotalAmount(BigDecimal.valueOf(23423));
            purchase.setPurchaseDate(purchaseDate);
            purchase.setCreatedBy(user);

            purchase2 = new Purchase();
            purchase2.setSupplier(supplier);
            purchase2.setPurchaseStatus(PurchaseStatus.RECEIVED);
            purchase2.setTotalAmount(BigDecimal.valueOf(23423));
            purchase2.setPurchaseDate(purchaseDate);
            purchase2.setCreatedBy(user);

            purchaseRepository.saveAll(List.of(purchase, purchase2));

        }

        @DisplayName("Should fetch all purchases")
        @Test
        void shouldReturnSuccessfulRequest_fetchAllPurchase() throws Exception {

            mockMvc.perform(get("/api/v1/purchase").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andDo(print());
        }

        @DisplayName("Should fetch specific purchase")
        @Test
        void shouldReturnSuccessfulRequest_fetchSpecificPurchase() throws Exception {
            Long id = purchase.getId();

            mockMvc.perform(get("/api/v1/purchase/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.supplierId").value(purchase.getSupplier().getId()))
                    .andDo(print());

        }

        @DisplayName("Should fail when id is invalid")
        @Test
        void shouldNotFoundRequest_invalidId() throws Exception {
            Long invalidId = 99L;

            mockMvc.perform(get("/api/v1/purchase/" + invalidId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("purchase not found"))
                    .andDo(print());
        }

    }

    @Nested
    class UpdatePurchaseTest {
        Role role;
        User user;
        Supplier supplier;
        LocalDate purchaseDate;
        Purchase purchase;
        Purchase purchase2;


        @BeforeEach
        void setup(){
            purchaseRepository.deleteAll();
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

            purchase = new Purchase();
            purchase.setSupplier(supplier);
            purchase.setPurchaseStatus(PurchaseStatus.PENDING);
            purchase.setTotalAmount(BigDecimal.valueOf(23423));
            purchase.setPurchaseDate(purchaseDate);
            purchase.setCreatedBy(user);

            purchase2 = new Purchase();
            purchase2.setSupplier(supplier);
            purchase2.setPurchaseStatus(PurchaseStatus.RECEIVED);
            purchase2.setTotalAmount(BigDecimal.valueOf(23423));
            purchase2.setPurchaseDate(purchaseDate);
            purchase2.setCreatedBy(user);

            purchaseRepository.saveAll(List.of(purchase, purchase2));

        }

        @DisplayName("Should update the purchase")
        @Test
        void shouldReturnSuccessfulRequest_updatePurchase() throws Exception {
            Long purchaseId = purchase.getId();

            PurchaseUpdateDTO purchaseUpdateDTO = new PurchaseUpdateDTO();
            purchaseUpdateDTO.setPurchaseStatus(PurchaseStatus.RECEIVED);

            mockMvc.perform(put("/api/v1/purchase/" + purchaseId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchaseUpdateDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());


        }

        @DisplayName("Should fail when there's already a pending purchase from the same supplier")
        @Test
        void shouldReturnConflictRequest_pendingPurchaseAlreadyExist() throws Exception {
            Long purchaseId = purchase2.getId();

            PurchaseUpdateDTO purchaseUpdateDTO = new PurchaseUpdateDTO();
            purchaseUpdateDTO.setPurchaseStatus(PurchaseStatus.PENDING);

            mockMvc.perform(put("/api/v1/purchase/" + purchaseId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchaseUpdateDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Supplier already has a different pending purchase."))
                    .andDo(print());


        }


    }

    @Nested
    class DeletePurchaseTest {
        Role role;
        User user;
        Supplier supplier;
        LocalDate purchaseDate;
        Purchase purchase;
        Purchase purchase2;


        @BeforeEach
        void setup(){
            purchaseRepository.deleteAll();
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

            purchase = new Purchase();
            purchase.setSupplier(supplier);
            purchase.setPurchaseStatus(PurchaseStatus.PENDING);
            purchase.setTotalAmount(BigDecimal.valueOf(23423));
            purchase.setPurchaseDate(purchaseDate);
            purchase.setCreatedBy(user);

            purchase2 = new Purchase();
            purchase2.setSupplier(supplier);
            purchase2.setPurchaseStatus(PurchaseStatus.RECEIVED);
            purchase2.setTotalAmount(BigDecimal.valueOf(23423));
            purchase2.setPurchaseDate(purchaseDate);
            purchase2.setCreatedBy(user);

            purchaseRepository.saveAll(List.of(purchase, purchase2));

        }

        @DisplayName("Should delete specific purchase")
        @Test
        void shouldReturnSuccessfulRequest_deletePurchase() throws Exception {
            Long id = purchase.getId();

            mockMvc.perform(delete("/api/v1/purchase/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }

        @DisplayName("Should fail when target id doesn't exist or invalid")
        @Test
        void shouldReturnNotFoundRequest_invalidId() throws Exception {
            Long id = 99L;

            mockMvc.perform(delete("/api/v1/purchase/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());

        }

    }


}