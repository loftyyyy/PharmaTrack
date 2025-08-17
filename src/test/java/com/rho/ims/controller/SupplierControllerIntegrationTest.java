package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.SupplierCreateDTO;
import com.rho.ims.dto.SupplierResponseDTO;
import com.rho.ims.dto.SupplierUpdateDTO;
import com.rho.ims.model.Role;
import com.rho.ims.model.Supplier;
import com.rho.ims.model.User;
import com.rho.ims.respository.RoleRepository;
import com.rho.ims.respository.SupplierRepository;
import com.rho.ims.respository.UserRepository;
import jakarta.transaction.Transactional;
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
@Transactional
class SupplierControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SupplierRepository supplierRepository;
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
    private UserRepository userRepository;


    @Nested
    class SupplierCreateTest {

        @BeforeEach
        void setup(){
            supplierRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();

            Role role = new Role();
            role.setName("staff");
            roleRepository.save(role);

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);

            userRepository.save(user);

            Supplier supplier = new Supplier();
            supplier.setName("TGP");
            supplier.setContactPerson("FIFAY");
            supplier.setEmail("tgp@gmail.com");
            supplier.setPhoneNumber("923423");
            supplier.setAddressCity("Davao");
            supplier.setAddressStreet("Matina");
            supplier.setAddressState("North Cot");
            supplier.setAddressZipCode("8000");
            supplier.setCreatedBy(user);

            supplierRepository.save(supplier);
        }

        @DisplayName("Should create supplier")
        @Test
        @WithMockUser(username = "user", roles = "Staff")
        void shouldReturnSuccessfulRequest_createSupplier() throws Exception {
            SupplierCreateDTO supplierCreateDTO = new SupplierCreateDTO();
            supplierCreateDTO.setName("Xyz Inc.");
            supplierCreateDTO.setContactPerson("James");
            supplierCreateDTO.setPhoneNumber("093823423");
            supplierCreateDTO.setEmail("xyz@gmail.com");
            supplierCreateDTO.setAddressCity("Davao");
            supplierCreateDTO.setAddressStreet("Matina");
            supplierCreateDTO.setAddressState("North Cot");
            supplierCreateDTO.setAddressZipCode("8000");

            mockMvc.perform(post("/api/v1/suppliers/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(supplierCreateDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }

        @WithMockUser(username = "user", roles = "Staff")
        @DisplayName("Should fail when there are blank or invalid field/s")
        @Test
        void shouldReturnBadRequest_blankFields() throws Exception {
            SupplierCreateDTO supplierCreateDTO = new SupplierCreateDTO();
            supplierCreateDTO.setName("");
            supplierCreateDTO.setContactPerson("James");
            supplierCreateDTO.setPhoneNumber("");
            supplierCreateDTO.setEmail("xyz@.com");
            supplierCreateDTO.setAddressCity("Davao");
            supplierCreateDTO.setAddressStreet("Matina");
            supplierCreateDTO.setAddressState("North Cot");
            supplierCreateDTO.setAddressZipCode("8000");

            mockMvc.perform(post("/api/v1/suppliers/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(supplierCreateDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());

        }

        @DisplayName("Should fail when there's already an existing supplier name")
        @Test
        void shouldReturnConflictRequest_existingName() throws Exception {
            SupplierCreateDTO supplierCreateDTO = new SupplierCreateDTO();
            supplierCreateDTO.setName("TGP");
            supplierCreateDTO.setContactPerson("James");
            supplierCreateDTO.setPhoneNumber("02934");
            supplierCreateDTO.setEmail("xyz@gmail.com");
            supplierCreateDTO.setAddressCity("Davao");
            supplierCreateDTO.setAddressStreet("Matina");
            supplierCreateDTO.setAddressState("North Cot");
            supplierCreateDTO.setAddressZipCode("8000");

            mockMvc.perform(post("/api/v1/suppliers/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(supplierCreateDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("name already exists"))
                    .andDo(print());
        }



    }

    @Nested
    class SupplierFetchTest {
        Supplier supplier;

        @BeforeEach
        void setup() {
            supplierRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();

            Role role = new Role();
            role.setName("staff");
            roleRepository.save(role);

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);

            userRepository.save(user);

            Supplier supplier = new Supplier();
            supplier.setName("TGP");
            supplier.setContactPerson("FIFAY");
            supplier.setEmail("tgp@gmail.com");
            supplier.setPhoneNumber("923423");
            supplier.setAddressCity("Davao");
            supplier.setAddressStreet("Matina");
            supplier.setAddressState("Davao del Sur");
            supplier.setAddressZipCode("8000");
            supplier.setCreatedBy(user);

            Supplier supplier2 = new Supplier();
            supplier2.setName("MD");
            supplier2.setContactPerson("FIFOY");
            supplier2.setEmail("MD@gmail.com");
            supplier2.setPhoneNumber("923423");
            supplier2.setAddressCity("Davao");
            supplier2.setAddressStreet("Gensan");
            supplier2.setAddressState("IDK");
            supplier2.setAddressZipCode("8000");
            supplier2.setCreatedBy(user);

            this.supplier = supplier;

            supplierRepository.saveAll(List.of(supplier, supplier2));

        }

        @DisplayName("Should return all suppliers")
        @Test
        void shouldReturnSuccessfulRequest_fetchAllSupplier() throws Exception {

            mockMvc.perform(get("/api/v1/suppliers").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andDo(print());

        }

        @DisplayName("Should fetch a specific supplier")
        @Test
        void shouldReturnSuccessfulRequest_fetchSpecificSupplier() throws Exception {
            long id = supplier.getId();

            mockMvc.perform(get("/api/v1/suppliers/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.id").value(id))
                    .andDo(print());

        }

        @DisplayName("Should fail when supplier id doesn't exist")
        @Test
        void shouldReturnNotFoundRequest_invalidSupplierId() throws Exception {
            Long id = 99L;

            mockMvc.perform(get("/api/v1/suppliers/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("supplier not found"))
                    .andDo(print());

        }


    }

    @Nested
    class SupplierUpdateTest {
        Supplier supplier;

        @BeforeEach
        void setup(){
            supplierRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();

            Role role = new Role();
            role.setName("staff");
            roleRepository.save(role);

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);

            userRepository.save(user);

            Supplier supplier = new Supplier();
            supplier.setName("TGP");
            supplier.setContactPerson("FIFAY");
            supplier.setEmail("tgp@gmail.com");
            supplier.setPhoneNumber("923423");
            supplier.setAddressCity("Davao");
            supplier.setAddressStreet("Matina");
            supplier.setAddressState("North Cot");
            supplier.setAddressZipCode("8000");
            supplier.setCreatedBy(user);

            Supplier supplier2 = new Supplier();
            supplier2.setName("MD");
            supplier2.setContactPerson("FIFOY");
            supplier2.setEmail("MD@gmail.com");
            supplier2.setPhoneNumber("923423");
            supplier2.setAddressCity("Davao");
            supplier2.setAddressStreet("Gensan");
            supplier2.setAddressState("IDK");
            supplier2.setAddressZipCode("8000");
            supplier2.setCreatedBy(user);

            this.supplier = supplier;
            supplierRepository.saveAll(List.of(supplier, supplier2));

        }

        @DisplayName("Should update the supplier")
        @Test
        void shouldReturnSuccessfulRequest_updateSupplier() throws Exception {
            long id = supplier.getId();

            SupplierUpdateDTO supplierUpdateDTO = new SupplierUpdateDTO();
            supplierUpdateDTO.setName("TGP");
            supplierUpdateDTO.setContactPerson("FIFOY");
            supplierUpdateDTO.setEmail("new@gmail.com");
            supplierUpdateDTO.setPhoneNumber("923423");
            supplierUpdateDTO.setAddressCity("Davao");
            supplierUpdateDTO.setAddressStreet("Matina");
            supplierUpdateDTO.setAddressState("Davao del Sur");
            supplierUpdateDTO.setAddressZipCode("8000");

            mockMvc.perform(put("/api/v1/suppliers/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(supplierUpdateDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }

        @DisplayName("Should fail when there are invalid fields")
        @Test
        void shouldReturnBadRequest_blankFields() throws Exception {

            long id = 1;

            SupplierUpdateDTO supplierUpdateDTO = new SupplierUpdateDTO();
            supplierUpdateDTO.setName("");
            supplierUpdateDTO.setContactPerson("James");
            supplierUpdateDTO.setEmail("new@gmail.com");
            supplierUpdateDTO.setPhoneNumber("923423");
            supplierUpdateDTO.setAddressCity("Davao");
            supplierUpdateDTO.setAddressStreet("Matina");
            supplierUpdateDTO.setAddressState("Davao del Sur");
            supplierUpdateDTO.setAddressZipCode("8000");

            mockMvc.perform(put("/api/v1/suppliers/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(supplierUpdateDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors['name']").value("Name is required"))
                    .andDo(print());


        }

        @DisplayName("Should fail when name already exists. Duplicate")
        @Test
        void shouldReturnConflictRequest_duplicateName() throws Exception {

            long id = 1;

            SupplierUpdateDTO supplierUpdateDTO = new SupplierUpdateDTO();
            supplierUpdateDTO.setName("MD");
            supplierUpdateDTO.setContactPerson("James");
            supplierUpdateDTO.setEmail("new@gmail.com");
            supplierUpdateDTO.setPhoneNumber("923423");
            supplierUpdateDTO.setAddressCity("Davao");
            supplierUpdateDTO.setAddressStreet("Matina");
            supplierUpdateDTO.setAddressState("Davao del Sur");
            supplierUpdateDTO.setAddressZipCode("8000");

            mockMvc.perform(put("/api/v1/suppliers/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(supplierUpdateDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("supplier already exists"))
                    .andDo(print());



        }

    }

    @Nested
    class SupplierDeleteTest {

        @BeforeEach
        void setup(){
            supplierRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();

            Role role = new Role();
            role.setName("staff");
            roleRepository.save(role);

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);

            userRepository.save(user);

            Supplier supplier = new Supplier();
            supplier.setName("TGP");
            supplier.setContactPerson("FIFAY");
            supplier.setEmail("tgp@gmail.com");
            supplier.setPhoneNumber("923423");
            supplier.setAddressCity("Davao");
            supplier.setAddressStreet("Matina");
            supplier.setAddressState("North Cot");
            supplier.setAddressZipCode("8000");
            supplier.setCreatedBy(user);

            Supplier supplier2 = new Supplier();
            supplier2.setName("MD");
            supplier2.setContactPerson("FIFOY");
            supplier2.setEmail("MD@gmail.com");
            supplier2.setPhoneNumber("923423");
            supplier2.setAddressCity("Davao");
            supplier2.setAddressStreet("Gensan");
            supplier2.setAddressState("IDK");
            supplier2.setAddressZipCode("8000");
            supplier2.setCreatedBy(user);

            supplierRepository.saveAll(List.of(supplier, supplier2));

        }

        @DisplayName("Should delete supplier")
        @Test
        void shouldReturnSuccessfulRequest_deleteSupplier() throws Exception {
            long id = 1;

            mockMvc.perform(delete("/api/v1/suppliers/"  + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().string("Supplier deleted successfully"))
                    .andDo(print());


        }

        @DisplayName("Should fail when id doesn't exist")
        @Test
        void shouldReturnNotFoundRequest_invalidId() throws Exception {
            Long id = 99L;

            mockMvc.perform(delete("/api/v1/suppliers/"  + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("supplier not found"))
                    .andDo(print());
        }
    }



}