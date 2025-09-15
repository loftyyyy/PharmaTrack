//package com.rho.ims.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rho.ims.config.SecurityConfig;
//import com.rho.ims.dto.ProductSupplierCreateDTO;
//import com.rho.ims.dto.ProductSupplierUpdateDTO;
//import com.rho.ims.model.*;
//import com.rho.ims.respository.*;
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
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Import(SecurityConfig.class)
//@TestPropertySource(locations = "classpath:application-test.properties")
//@ActiveProfiles("test")
//class ProductSupplierControllerIntegrationTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
//    @Autowired
//    private RoleRepository roleRepository;
//
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
//    private CategoryRepository categoryRepository;
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private SupplierRepository supplierRepository;
//    @Autowired
//    private ProductSupplierRepository productSupplierRepository;
//
//    @Nested
//    @WithMockUser(username = "user", roles = "Staff")
//    class CreateProductSupplierTest {
//        Role role;
//        User user;
//        Category category;
//        Product product;
//        Product product2;
//        Supplier supplier;
//        ProductSupplier productSupplier;
//
//
//        @BeforeEach
//        void setup(){
//            productSupplierRepository.deleteAll();
//            supplierRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//
//            role = new Role();
//            role.setName("admin");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            user.setRole(role);
//            userRepository.save(user);
//
//            category = new Category();
//            category.setName("Pain Relief");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Paracetamol");
//            product.setBarcode("xyz");
//            product.setBrand("Generic");
//            product.setCategory(category);
//            product.setDescription("For headaches");
//            product.setCreatedBy(user);
//
//            product2 = new Product();
//            product2.setName("Advil");
//            product2.setBarcode("yxz");
//            product2.setBrand("Generic");
//            product2.setCategory(category);
//            product2.setDescription("For pain relief");
//            product2.setCreatedBy(user);
//
//            productRepository.saveAll(List.of(product,product2));
//
//            supplier = new Supplier();
//            supplier.setName("Mercury Drug");
//            supplier.setContactPerson("James");
//            supplier.setPhoneNumber("0923423");
//            supplier.setEmail("md@gmail.com");
//            supplier.setAddressState("Cotabato");
//            supplier.setAddressCity("Gensan");
//            supplier.setAddressZipCode("8000");
//            supplier.setCreatedBy(user);
//            supplier.setAddressStreet("Rizal Street");
//
//            supplierRepository.save(supplier);
//
//
//            productSupplier = new ProductSupplier();
//            productSupplier.setProduct(product);
//            productSupplier.setSupplier(supplier);
//            productSupplier.setSupplierProductCode("32458");
//            productSupplier.setPreferredSupplier(true);
//
//            productSupplierRepository.save(productSupplier);
//
//        }
//
//        @DisplayName("Should create new product supplier")
//        @Test
//        void shouldReturnSuccessfulRequest_createProductSupplier() throws Exception {
//            ProductSupplierCreateDTO productSupplierCreateDTO = new ProductSupplierCreateDTO();
//            productSupplierCreateDTO.setProductId(product2.getId());
//            productSupplierCreateDTO.setSupplierId(supplier.getId());
//            productSupplierCreateDTO.setPreferredSupplier(true);
//            productSupplierCreateDTO.setSupplierProductCode("73654321");
//
//            mockMvc.perform(post("/api/v1/productSuppliers/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productSupplierCreateDTO)))
//                    .andExpect(status().is2xxSuccessful())
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when there's already an existing product id and supplier id in another product supplier")
//        @Test
//        void shouldReturnConflictRequest_duplicateProductSupplier() throws Exception {
//            ProductSupplierCreateDTO productSupplierCreateDTO = new ProductSupplierCreateDTO();
//            productSupplierCreateDTO.setProductId(product.getId());
//            productSupplierCreateDTO.setSupplierId(supplier.getId());
//            productSupplierCreateDTO.setPreferredSupplier(true);
//            productSupplierCreateDTO.setSupplierProductCode("73654321");
//
//            mockMvc.perform(post("/api/v1/productSuppliers/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productSupplierCreateDTO)))
//                    .andExpect(status().isConflict())
//                    .andExpect(jsonPath("$.message").value("product supplier already exists"))
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when there's already an existing supplier id and product code in the same supplier")
//        @Test
//        void shouldReturnConflictRequest_duplicateProductCode() throws Exception {
//            ProductSupplierCreateDTO productSupplierCreateDTO = new ProductSupplierCreateDTO();
//            productSupplierCreateDTO.setProductId(product2.getId());
//            productSupplierCreateDTO.setSupplierId(supplier.getId());
//            productSupplierCreateDTO.setPreferredSupplier(true);
//            productSupplierCreateDTO.setSupplierProductCode("32458");
//
//            mockMvc.perform(post("/api/v1/productSuppliers/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productSupplierCreateDTO)))
//                    .andExpect(status().isConflict())
//                    .andExpect(jsonPath("$.message").value("Supplier product code already exists for this supplier"))
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when there's missing field/s")
//        @Test
//        void shouldReturnBadRequest_missingFields() throws Exception {
//
//            ProductSupplierCreateDTO productSupplierCreateDTO = new ProductSupplierCreateDTO();
//            productSupplierCreateDTO.setProductId(null);
//            productSupplierCreateDTO.setSupplierId(null);
//            productSupplierCreateDTO.setPreferredSupplier(true);
//            productSupplierCreateDTO.setSupplierProductCode("73654321");
//
//            mockMvc.perform(post("/api/v1/productSuppliers/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productSupplierCreateDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors['supplierId']").value("Supplier is required"))
//                    .andExpect(jsonPath("$.fieldErrors['productId']").value("Product is required"))
//                    .andDo(print());
//        }
//
//
//    }
//
//    @Nested
//    class FetchProductSupplierTest {
//        Role role;
//        User user;
//        Category category;
//        Product product;
//        Product product2;
//        Supplier supplier;
//        ProductSupplier productSupplier;
//        ProductSupplier productSupplier2;
//
//
//        @BeforeEach
//        void setup(){
//            productSupplierRepository.deleteAll();
//            supplierRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//
//            role = new Role();
//            role.setName("admin");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            user.setRole(role);
//            userRepository.save(user);
//
//            category = new Category();
//            category.setName("Pain Relief");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Paracetamol");
//            product.setBarcode("xyz");
//            product.setBrand("Generic");
//            product.setCategory(category);
//            product.setDescription("For headaches");
//            product.setCreatedBy(user);
//
//            product2 = new Product();
//            product2.setName("Advil");
//            product2.setBarcode("yxz");
//            product2.setBrand("Generic");
//            product2.setCategory(category);
//            product2.setDescription("For pain relief");
//            product2.setCreatedBy(user);
//
//            productRepository.saveAll(List.of(product,product2));
//
//            supplier = new Supplier();
//            supplier.setName("Mercury Drug");
//            supplier.setContactPerson("James");
//            supplier.setPhoneNumber("0923423");
//            supplier.setEmail("md@gmail.com");
//            supplier.setAddressState("Cotabato");
//            supplier.setAddressCity("Gensan");
//            supplier.setAddressZipCode("8000");
//            supplier.setCreatedBy(user);
//            supplier.setAddressStreet("Rizal Street");
//
//            supplierRepository.save(supplier);
//
//
//            productSupplier = new ProductSupplier();
//            productSupplier.setProduct(product);
//            productSupplier.setSupplier(supplier);
//            productSupplier.setSupplierProductCode("32458B");
//            productSupplier.setPreferredSupplier(true);
//
//
//            productSupplier2 = new ProductSupplier();
//            productSupplier2.setProduct(product2);
//            productSupplier2.setSupplier(supplier);
//            productSupplier2.setSupplierProductCode("32458");
//            productSupplier2.setPreferredSupplier(true);
//
//            productSupplierRepository.saveAll(List.of(productSupplier, productSupplier2));
//
//        }
//
//        @DisplayName("Should fetch all product suppliers")
//        @Test
//        void shouldReturnSuccessfulRequest_fetchAllProductSuppliers() throws Exception {
//            mockMvc.perform(get("/api/v1/productSuppliers").contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().is2xxSuccessful())
//                    .andExpect(jsonPath("$.length()").value(2))
//                    .andDo(print());
//
//        }
//
//
//        @DisplayName("Should return specific product supplier")
//        @Test
//        void shouldReturnSuccessfulRequest_fetchSpecificProductSupplier() throws Exception {
//            Long productSupplierId = productSupplier.getId();
//
//            mockMvc.perform(get("/api/v1/productSuppliers/" + productSupplierId))
//                    .andExpect(status().is2xxSuccessful())
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when specific product supplier id doesn't exist")
//        @Test
//        void shouldReturnNotFoundRequest_invalidProductSupplier() throws Exception {
//            Long nonExistentProductSupplierId = 99L;
//
//            mockMvc.perform(get("/api/v1/productSuppliers/" + nonExistentProductSupplierId))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.message").value("product supplier not found"))
//                    .andDo(print());
//
//        }
//
//    }
//
//    @Nested
//    class UpdateProductSupplierTest {
//        Role role;
//        User user;
//        Category category;
//        Product product;
//        Product product2;
//        Supplier supplier;
//        ProductSupplier productSupplier;
//        ProductSupplier productSupplier2;
//
//
//        @BeforeEach
//        void setup(){
//            productSupplierRepository.deleteAll();
//            supplierRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//
//            role = new Role();
//            role.setName("admin");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            user.setRole(role);
//            userRepository.save(user);
//
//            category = new Category();
//            category.setName("Pain Relief");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Paracetamol");
//            product.setBarcode("xyz");
//            product.setBrand("Generic");
//            product.setCategory(category);
//            product.setDescription("For headaches");
//            product.setCreatedBy(user);
//
//            product2 = new Product();
//            product2.setName("Advil");
//            product2.setBarcode("yxz");
//            product2.setBrand("Generic");
//            product2.setCategory(category);
//            product2.setDescription("For pain relief");
//            product2.setCreatedBy(user);
//
//            productRepository.saveAll(List.of(product,product2));
//
//            supplier = new Supplier();
//            supplier.setName("Mercury Drug");
//            supplier.setContactPerson("James");
//            supplier.setPhoneNumber("0923423");
//            supplier.setEmail("md@gmail.com");
//            supplier.setAddressState("Cotabato");
//            supplier.setAddressCity("Gensan");
//            supplier.setAddressZipCode("8000");
//            supplier.setCreatedBy(user);
//            supplier.setAddressStreet("Rizal Street");
//
//            supplierRepository.save(supplier);
//
//
//            productSupplier = new ProductSupplier();
//            productSupplier.setProduct(product);
//            productSupplier.setSupplier(supplier);
//            productSupplier.setSupplierProductCode("32458B");
//            productSupplier.setPreferredSupplier(true);
//
//
//            productSupplier2 = new ProductSupplier();
//            productSupplier2.setProduct(product2);
//            productSupplier2.setSupplier(supplier);
//            productSupplier2.setSupplierProductCode("32458");
//            productSupplier2.setPreferredSupplier(true);
//
//            productSupplierRepository.saveAll(List.of(productSupplier, productSupplier2));
//
//        }
//
//        @DisplayName("Should update the product supplier")
//        @Test
//        void shouldReturnSuccessfulRequest_updateProductSupplier() throws Exception {
//            Long productSupplierId = productSupplier.getId();
//
//            ProductSupplierUpdateDTO productSupplierUpdateDTO = new ProductSupplierUpdateDTO();
//            productSupplierUpdateDTO.setPreferredSupplier(false);
//            productSupplierUpdateDTO.setSupplierProductCode("3214532A");
//
//            mockMvc.perform(put("/api/v1/productSuppliers/" + productSupplierId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productSupplierUpdateDTO)))
//                    .andExpect(status().is2xxSuccessful())
//                    .andDo(print());
//        }
//
//        @DisplayName("Should fail when product code already exists in the same supplier")
//        @Test
//        void shouldReturnConflictRequest_existingProductCodeSameSupplier() throws Exception {
//            Long productSupplierId = productSupplier.getId();
//
//            ProductSupplierUpdateDTO productSupplierUpdateDTO = new ProductSupplierUpdateDTO();
//            productSupplierUpdateDTO.setPreferredSupplier(false);
//            productSupplierUpdateDTO.setSupplierProductCode("32458");
//
//            mockMvc.perform(put("/api/v1/productSuppliers/" + productSupplierId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productSupplierUpdateDTO)))
//                    .andExpect(status().isConflict())
//                    .andDo(print());
//
//        }
//
//    }
//
//    @Nested
//    class DeleteProductSupplierTest {
//
//        Role role;
//        User user;
//        Category category;
//        Product product;
//        Product product2;
//        Supplier supplier;
//        ProductSupplier productSupplier;
//        ProductSupplier productSupplier2;
//
//
//        @BeforeEach
//        void setup(){
//            productSupplierRepository.deleteAll();
//            supplierRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//
//            role = new Role();
//            role.setName("admin");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            user.setRole(role);
//            userRepository.save(user);
//
//            category = new Category();
//            category.setName("Pain Relief");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Paracetamol");
//            product.setBarcode("xyz");
//            product.setBrand("Generic");
//            product.setCategory(category);
//            product.setDescription("For headaches");
//            product.setCreatedBy(user);
//
//            product2 = new Product();
//            product2.setName("Advil");
//            product2.setBarcode("yxz");
//            product2.setBrand("Generic");
//            product2.setCategory(category);
//            product2.setDescription("For pain relief");
//            product2.setCreatedBy(user);
//
//            productRepository.saveAll(List.of(product,product2));
//
//            supplier = new Supplier();
//            supplier.setName("Mercury Drug");
//            supplier.setContactPerson("James");
//            supplier.setPhoneNumber("0923423");
//            supplier.setEmail("md@gmail.com");
//            supplier.setAddressState("Cotabato");
//            supplier.setAddressCity("Gensan");
//            supplier.setAddressZipCode("8000");
//            supplier.setCreatedBy(user);
//            supplier.setAddressStreet("Rizal Street");
//
//            supplierRepository.save(supplier);
//
//
//            productSupplier = new ProductSupplier();
//            productSupplier.setProduct(product);
//            productSupplier.setSupplier(supplier);
//            productSupplier.setSupplierProductCode("32458B");
//            productSupplier.setPreferredSupplier(true);
//
//
//            productSupplier2 = new ProductSupplier();
//            productSupplier2.setProduct(product2);
//            productSupplier2.setSupplier(supplier);
//            productSupplier2.setSupplierProductCode("32458");
//            productSupplier2.setPreferredSupplier(true);
//
//            productSupplierRepository.saveAll(List.of(productSupplier, productSupplier2));
//
//        }
//
//        @DisplayName("Should delete product supplier")
//        @Test
//        void shouldReturnSuccessfulRequest_deleteProductSupplier() throws Exception {
//            Long productSupplierId = productSupplier.getId();
//
//            mockMvc.perform(delete("/api/v1/productSuppliers/" + productSupplierId).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().is2xxSuccessful())
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when product supplier id doesn't exist or null")
//        @Test
//        void shouldReturnNotFoundRequest_invalidProductSupplierId() throws Exception {
//            Long nonExistentProductSupplierId = 99L;
//
//            mockMvc.perform(delete("/api/v1/productSuppliers/" + nonExistentProductSupplierId).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isNotFound())
//                    .andDo(print());
//
//        }
//
//    }
//
//
//
//}