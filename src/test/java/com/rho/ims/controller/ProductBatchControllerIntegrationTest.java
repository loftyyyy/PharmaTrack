//package com.rho.ims.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rho.ims.config.SecurityConfig;
//import com.rho.ims.dto.ProductBatchCreateDTO;
//import com.rho.ims.dto.ProductBatchUpdateDTO;
//import com.rho.ims.model.*;
//import com.rho.ims.respository.*;
//import org.hibernate.grammars.hql.HqlParser;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.parameters.P;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Import(SecurityConfig.class)
//@TestPropertySource(locations = "classpath:application-test.properties")
//@ActiveProfiles("test")
//class ProductBatchControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private CategoryRepository categoryRepository;
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Value("${test.user.username}")
//    private String username;
//
//    @Value("${test.user.email}")
//    private String email;
//
//    @Value("${test.user.password}")
//    private String password;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private ProductBatchRepository productBatchRepository;
//
//
//    @Nested
//    @WithMockUser(username = "user", roles = "Staff")
//    class ProductBatchCreateTest {
//        Product product;
//        Category category;
//        User user;
//        Role role;
//        ProductBatch productBatch;
//
//        LocalDate expiryDate;
//        LocalDate manufactureDate;
//
//        @BeforeEach
//        void setup(){
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//
//
//
//            role = new Role();
//            role.setName("staff");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setRole(role);
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            userRepository.save(user);
//
//            category = new Category();
//            category.setName("Pain Relief");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Paracetamol");
//            product.setBrand("Generic");
//            product.setCategory(category);
//            product.setBarcode("xyz123");
//            product.setCreatedBy(user);
//            productRepository.save(product);
//
//
//            expiryDate = LocalDate.of(2029,6,25);
//            manufactureDate = LocalDate.of(2025, 8, 30);
//
//            productBatch = new ProductBatch();
//            productBatch.setProduct(product);
//            productBatch.setBatchNumber("xy1234");
//            productBatch.setQuantity(10);
//            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(320));
//            productBatch.setLocation("Japan");
//            productBatch.setExpiryDate(expiryDate);
//            productBatch.setManufacturingDate(manufactureDate);
//            productBatch.setCreatedBy(user);
//
//            productBatchRepository.save(productBatch);
//
//
//        }
//
//        @DisplayName("Should create product batch")
//        @Test
//        void shouldReturnSuccessfulRequest_createProductBatch() throws Exception {
//            ProductBatchCreateDTO productBatchCreateDTO = new ProductBatchCreateDTO();
//            productBatchCreateDTO.setProductId(this.product.getId());
//            productBatchCreateDTO.setBatchNumber("ABCDEFG1234");
//            productBatchCreateDTO.setQuantity(30);
//            productBatchCreateDTO.setLocation("China");
//            productBatchCreateDTO.setPurchasePricePerUnit(BigDecimal.valueOf(300));
//            productBatchCreateDTO.setExpiryDate(expiryDate);
//            productBatchCreateDTO.setManufacturingDate(manufactureDate);
//
//            mockMvc.perform(post("/api/v1/productBatches/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productBatchCreateDTO)))
//                    .andExpect(status().is2xxSuccessful())
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when field/s are missing")
//        @Test
//        void shouldReturnBadRequest_missingFields() throws Exception {
//            ProductBatchCreateDTO productBatchCreateDTO = new ProductBatchCreateDTO();
//            productBatchCreateDTO.setProductId(this.product.getId());
//            productBatchCreateDTO.setBatchNumber("");
//            productBatchCreateDTO.setQuantity(30);
//            productBatchCreateDTO.setLocation("China");
//            productBatchCreateDTO.setPurchasePricePerUnit(BigDecimal.valueOf(300));
//            productBatchCreateDTO.setExpiryDate(expiryDate);
//            productBatchCreateDTO.setManufacturingDate(manufactureDate);
//
//            mockMvc.perform(post("/api/v1/productBatches/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productBatchCreateDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors['batchNumber']").value("Batch number is required"))
//                    .andDo(print());
//
//
//        }
//
//        @DisplayName("Should fail if there's already an existing product batch number")
//        @Test
//        void shouldReturnConflictRequest_duplicateProductBatch() throws Exception {
//
//            ProductBatchCreateDTO productBatchCreateDTO = new ProductBatchCreateDTO();
//            productBatchCreateDTO.setProductId(this.product.getId());
//            productBatchCreateDTO.setBatchNumber("xy1234");
//            productBatchCreateDTO.setQuantity(30);
//            productBatchCreateDTO.setLocation("China");
//            productBatchCreateDTO.setPurchasePricePerUnit(BigDecimal.valueOf(300));
//            productBatchCreateDTO.setExpiryDate(expiryDate);
//            productBatchCreateDTO.setManufacturingDate(manufactureDate);
//
//            mockMvc.perform(post("/api/v1/productBatches/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productBatchCreateDTO)))
//                    .andExpect(status().isConflict())
//                    .andExpect(jsonPath("$.message").value("batch number already exists"))
//                    .andDo(print());
//
//        }
//
//
//        @DisplayName("Should fail when product doesn't exist")
//        @Test
//        void shouldReturnNotFoundRequest_invalidProduct() throws Exception {
//            Long nonExistentId = 99L;
//
//            ProductBatchCreateDTO productBatchCreateDTO = new ProductBatchCreateDTO();
//            productBatchCreateDTO.setProductId(nonExistentId);
//            productBatchCreateDTO.setBatchNumber("ABCDEFG");
//            productBatchCreateDTO.setQuantity(30);
//            productBatchCreateDTO.setLocation("China");
//            productBatchCreateDTO.setPurchasePricePerUnit(BigDecimal.valueOf(300));
//            productBatchCreateDTO.setExpiryDate(expiryDate);
//            productBatchCreateDTO.setManufacturingDate(manufactureDate);
//
//            mockMvc.perform(post("/api/v1/productBatches/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productBatchCreateDTO)))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.message").value("product not found"))
//                    .andDo(print());
//        }
//
//
//    }
//
//
//    @Nested
//    @WithMockUser(username = "user", roles = "Staff")
//    class ProductBatchFetchTest {
//        Product product;
//        Category category;
//        User user;
//        Role role;
//        ProductBatch productBatch;
//        ProductBatch productBatch2;
//
//        LocalDate expiryDate;
//        LocalDate manufactureDate;
//
//        @BeforeEach
//        void setup(){
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//
//
//
//            role = new Role();
//            role.setName("staff");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setRole(role);
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            userRepository.save(user);
//
//            category = new Category();
//            category.setName("Pain Relief");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Paracetamol");
//            product.setBrand("Generic");
//            product.setCategory(category);
//            product.setBarcode("xyz123");
//            product.setCreatedBy(user);
//            productRepository.save(product);
//
//
//            expiryDate = LocalDate.of(2029,6,25);
//            manufactureDate = LocalDate.of(2025, 8, 30);
//
//            productBatch = new ProductBatch();
//            productBatch.setProduct(product);
//            productBatch.setBatchNumber("xy1234");
//            productBatch.setQuantity(10);
//            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(320));
//            productBatch.setLocation("Japan");
//            productBatch.setExpiryDate(expiryDate);
//            productBatch.setManufacturingDate(manufactureDate);
//            productBatch.setCreatedBy(user);
//
//
//            productBatch2 = new ProductBatch();
//            productBatch2.setProduct(product);
//            productBatch2.setBatchNumber("ABCDE1234");
//            productBatch2.setQuantity(10);
//            productBatch2.setPurchasePricePerUnit(BigDecimal.valueOf(380));
//            productBatch2.setLocation("Geneva");
//            productBatch2.setExpiryDate(expiryDate);
//            productBatch2.setManufacturingDate(manufactureDate);
//            productBatch2.setCreatedBy(user);
//
//            productBatchRepository.saveAll(List.of(productBatch, productBatch2));
//
//
//        }
//
//
//        @DisplayName("Should fetch all product batches")
//        @Test
//        void shouldReturnSuccessfulRequest_fetchAllProductBatch() throws Exception {
//
//            mockMvc.perform(get("/api/v1/productBatches").contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().is2xxSuccessful())
//                    .andExpect(jsonPath("$.length()").value(2))
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fetch a specific product batch")
//        @Test
//        void shouldReturnSuccessfulRequest_fetchSpecificProductBatch() throws Exception {
//            Long id = productBatch.getId();
//
//            mockMvc.perform(get("/api/v1/productBatches/" + id).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().is2xxSuccessful())
//                    .andExpect(jsonPath("$.batchNumber").value(productBatch.getBatchNumber()))
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when product batch id doesn't exist")
//        @Test
//        void shouldReturnNotFoundRequest_badProductBatchId() throws Exception {
//            Long nonExistentId = 99L;
//
//            mockMvc.perform(get("/api/v1/productBatches/" + nonExistentId).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.message").value("product batch id not found"))
//                    .andDo(print());
//
//        }
//
//
//    }
//
//    @Nested
//    @WithMockUser(username = "user", roles = "Staff")
//    class ProductBatchUpdateTest {
//        Product product;
//        Category category;
//        User user;
//        Role role;
//        ProductBatch productBatch;
//        ProductBatch productBatch2;
//
//        LocalDate expiryDate;
//        LocalDate manufactureDate;
//
//        @BeforeEach
//        void setup(){
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//
//
//
//            role = new Role();
//            role.setName("staff");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setRole(role);
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            userRepository.save(user);
//
//            category = new Category();
//            category.setName("Pain Relief");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Paracetamol");
//            product.setBrand("Generic");
//            product.setCategory(category);
//            product.setBarcode("xyz123");
//            product.setCreatedBy(user);
//            productRepository.save(product);
//
//
//            expiryDate = LocalDate.of(2029,6,25);
//            manufactureDate = LocalDate.of(2025, 8, 30);
//
//            productBatch = new ProductBatch();
//            productBatch.setProduct(product);
//            productBatch.setBatchNumber("xy1234");
//            productBatch.setQuantity(10);
//            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(320));
//            productBatch.setLocation("Japan");
//            productBatch.setExpiryDate(expiryDate);
//            productBatch.setManufacturingDate(manufactureDate);
//            productBatch.setCreatedBy(user);
//
//
//            productBatch2 = new ProductBatch();
//            productBatch2.setProduct(product);
//            productBatch2.setBatchNumber("ABCDE1234");
//            productBatch2.setQuantity(10);
//            productBatch2.setPurchasePricePerUnit(BigDecimal.valueOf(380));
//            productBatch2.setLocation("Geneva");
//            productBatch2.setExpiryDate(expiryDate);
//            productBatch2.setManufacturingDate(manufactureDate);
//            productBatch2.setCreatedBy(user);
//
//            productBatchRepository.saveAll(List.of(productBatch, productBatch2));
//
//        }
//
//        @DisplayName("Should update the product batch")
//        @Test
//        void shouldReturnSuccessfulRequest_updateProductBatch() throws Exception {
//            Long productBatchId = productBatch.getId();
//
//            ProductBatchUpdateDTO productBatchUpdateDTO = new ProductBatchUpdateDTO();
//            productBatchUpdateDTO.setPurchasePricePerUnit(BigDecimal.valueOf(350));
//            productBatchUpdateDTO.setExpiryDate(expiryDate);
//            productBatchUpdateDTO.setManufacturingDate(manufactureDate);
//            productBatchUpdateDTO.setQuantity(75);
//            productBatchUpdateDTO.setLocation("Japan");
//
//            mockMvc.perform(put("/api/v1/productBatches/" + productBatchId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productBatchUpdateDTO)))
//                    .andExpect(status().is2xxSuccessful())
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when product batch id doesn't exist")
//        @Test
//        void shouldReturnNotFoundRequest_invalidProductBatchId() throws Exception {
//            Long nonExistentProductBatchId = 99L;
//
//
//            ProductBatchUpdateDTO productBatchUpdateDTO = new ProductBatchUpdateDTO();
//            productBatchUpdateDTO.setPurchasePricePerUnit(BigDecimal.valueOf(350));
//            productBatchUpdateDTO.setExpiryDate(expiryDate);
//            productBatchUpdateDTO.setManufacturingDate(manufactureDate);
//            productBatchUpdateDTO.setQuantity(75);
//            productBatchUpdateDTO.setLocation("Japan");
//
//            mockMvc.perform(put("/api/v1/productBatches/" + nonExistentProductBatchId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productBatchUpdateDTO)))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.message").value("product batch not found"))
//                    .andDo(print());
//
//
//        }
//
//
//    }
//
//    @Nested
//    @WithMockUser(username = "user", roles = "Staff")
//    class ProductBatchDeleteTest {
//        Product product;
//        Category category;
//        User user;
//        Role role;
//        ProductBatch productBatch;
//        ProductBatch productBatch2;
//
//        LocalDate expiryDate;
//        LocalDate manufactureDate;
//
//        @BeforeEach
//        void setup(){
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//
//
//
//            role = new Role();
//            role.setName("staff");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setRole(role);
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            userRepository.save(user);
//
//            category = new Category();
//            category.setName("Pain Relief");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Paracetamol");
//            product.setBrand("Generic");
//            product.setCategory(category);
//            product.setBarcode("xyz123");
//            product.setCreatedBy(user);
//            productRepository.save(product);
//
//
//            expiryDate = LocalDate.of(2029,6,25);
//            manufactureDate = LocalDate.of(2025, 8, 30);
//
//            productBatch = new ProductBatch();
//            productBatch.setProduct(product);
//            productBatch.setBatchNumber("xy1234");
//            productBatch.setQuantity(10);
//            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(320));
//            productBatch.setLocation("Japan");
//            productBatch.setExpiryDate(expiryDate);
//            productBatch.setManufacturingDate(manufactureDate);
//            productBatch.setCreatedBy(user);
//
//
//            productBatch2 = new ProductBatch();
//            productBatch2.setProduct(product);
//            productBatch2.setBatchNumber("ABCDE1234");
//            productBatch2.setQuantity(10);
//            productBatch2.setPurchasePricePerUnit(BigDecimal.valueOf(380));
//            productBatch2.setLocation("Geneva");
//            productBatch2.setExpiryDate(expiryDate);
//            productBatch2.setManufacturingDate(manufactureDate);
//            productBatch2.setCreatedBy(user);
//
//            productBatchRepository.saveAll(List.of(productBatch, productBatch2));
//
//        }
//
//        @DisplayName("Should delete product batch")
//        @Test
//        void shouldReturnSuccessfulRequest_deleteProductBatch() throws Exception {
//            Long productBatchId = productBatch.getId();
//
//            mockMvc.perform(delete("/api/v1/productBatches/" + productBatchId).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().is2xxSuccessful())
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when product batch id doesn't exist")
//        @Test
//        void shouldReturnNotFoundRequest_invalidProductBatchId() throws Exception {
//            Long nonExistentProductBatchId = 99L;
//
//            mockMvc.perform(delete("/api/v1/productBatch//" + nonExistentProductBatchId).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isNotFound())
//                    .andDo(print());
//
//
//        }
//
//    }
//
//
//
//
//
//
//
//
//
//
//
//}