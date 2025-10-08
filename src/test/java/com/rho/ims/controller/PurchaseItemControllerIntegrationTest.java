//package com.rho.ims.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rho.ims.config.SecurityConfig;
//import com.rho.ims.dto.purchase.PurchaseCreateDTO;
//import com.rho.ims.dto.PurchaseItemCreateDTO;
//import com.rho.ims.dto.PurchaseItemUpdateDTO;
//import com.rho.ims.dto.purchase.PurchaseUpdateDTO;
//import com.rho.ims.enums.PurchaseStatus;
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
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Import(SecurityConfig.class)
//@TestPropertySource(locations = "classpath:application-test.properties")
//@ActiveProfiles("test")
//@WithMockUser(username = "user", roles = "Staff")
//class PurchaseItemControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private PurchaseRepository purchaseRepository;
//    @Autowired
//    private SupplierRepository supplierRepository;
//    @Autowired
//    private ProductBatchRepository productBatchRepository;
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private CategoryRepository categoryRepository;
//    @Autowired
//    private UserRepository userRepository;
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
//    private PurchaseItemRepository purchaseItemRepository;
//
//    @Nested
//    class PurchaseItemCreateTest {
//        Role role;
//        User user;
//        Category category;
//        Product product;
//        ProductBatch productBatch;
//        Supplier supplier;
//        Purchase purchase;
//        Purchase purchase2;
//        PurchaseItem purchaseItem;
//
//
//        @BeforeEach
//        void setup() {
//            purchaseItemRepository.deleteAll();
//            purchaseRepository.deleteAll();
//            supplierRepository.deleteAll();
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            role = new Role();
//            role.setName("Admin");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            user.setRole(role);
//            userRepository.save(user);
//
//
//            category = new Category();
//            category.setName("Pain Killer");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Advil");
//            product.setCategory(category);
//            product.setBrand("Vulcasil");
//            product.setBarcode("1234xyz");
//            product.setDescription("For pain relief");
//            product.setCreatedBy(user);
//            productRepository.save(product);
//
//            productBatch = new ProductBatch();
//            productBatch.setProduct(product);
//            productBatch.setLocation("Gensan");
//            productBatch.setExpiryDate(LocalDate.of(2029,8,19));
//            productBatch.setManufacturingDate(LocalDate.now());
//            productBatch.setQuantity(120);
//            productBatch.setBatchNumber("xyz1234");
//            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(32));
//            productBatch.setCreatedBy(user);
//            productBatchRepository.save(productBatch);
//
//            supplier = new Supplier();
//            supplier.setContactPerson("James");
//            supplier.setPhoneNumber("j0923432");
//            supplier.setName("TGP");
//            supplier.setEmail("tgp@gmail.com");
//            supplier.setAddressCity("M'lang");
//            supplier.setAddressState("Cotabato");
//            supplier.setAddressStreet("Garcia Street");
//            supplier.setAddressZipCode("9402");
//            supplier.setCreatedBy(user);
//            supplierRepository.save(supplier);
//
//            purchase = new Purchase();
//            purchase.setSupplier(supplier);
//            purchase.setPurchaseDate(LocalDate.now());
//            purchase.setPurchaseStatus(PurchaseStatus.RECEIVED);
//            purchase.setTotalAmount(BigDecimal.valueOf(3200));
//            purchase.setCreatedBy(user);
//
//            purchase2 = new Purchase();
//            purchase2.setSupplier(supplier);
//            purchase2.setPurchaseDate(LocalDate.now());
//            purchase2.setPurchaseStatus(PurchaseStatus.RECEIVED);
//            purchase2.setTotalAmount(BigDecimal.valueOf(3200));
//            purchase2.setCreatedBy(user);
//            purchaseRepository.saveAll(List.of(purchase, purchase2));
//
//            purchaseItem =  new PurchaseItem();
//            purchaseItem.setProductBatch(productBatch);
//            purchaseItem.setPurchase(purchase);
//            purchaseItem.setQuantity(32);
//            purchaseItem.setUnitPrice(BigDecimal.valueOf(100));
//            purchaseItemRepository.save(purchaseItem);
//        }
//
//        @DisplayName("Should create purchase item")
//        @Test
//        void shouldReturnSuccessfulRequest_createPurchaseItem() throws Exception {
//            Long productBatchId = productBatch.getId();
//            Long purchaseId = purchase2.getId();
//
//            PurchaseItemCreateDTO purchaseItemCreateDTO = new PurchaseItemCreateDTO();
//            purchaseItemCreateDTO.setProductBatchId(productBatchId);
//            purchaseItemCreateDTO.setPurchaseId(purchaseId);
//            purchaseItemCreateDTO.setQuantity(12);
//            purchaseItemCreateDTO.setUnitPrice(BigDecimal.valueOf(125));
//
//            mockMvc.perform(post("/api/v1/purchaseItems/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchaseItemCreateDTO)))
//                    .andExpect(status().is2xxSuccessful())
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when there's missing fields in the creation")
//        @Test
//        void shouldReturnBadRequest_missingFields() throws Exception {
//            PurchaseItemCreateDTO purchaseItemCreateDTO = new PurchaseItemCreateDTO();
//            purchaseItemCreateDTO.setPurchaseId(null);
//            purchaseItemCreateDTO.setProductBatchId(null);
//            purchaseItemCreateDTO.setQuantity(12);
//            purchaseItemCreateDTO.setUnitPrice(BigDecimal.valueOf(125));
//
//
//            mockMvc.perform(post("/api/v1/purchaseItems/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchaseItemCreateDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andDo(print());
//        }
//
//        @DisplayName("Should fail when purchase item already exist with the same product batch and purchase")
//        @Test
//        void shouldReturnConflictRequest_duplicatePurchaseItem() throws Exception {
//            Long productBatchId = productBatch.getId();
//            Long purchaseId = purchase.getId();
//
//            PurchaseItemCreateDTO purchaseItemCreateDTO = new PurchaseItemCreateDTO();
//            purchaseItemCreateDTO.setProductBatchId(productBatchId);
//            purchaseItemCreateDTO.setPurchaseId(purchaseId);
//            purchaseItemCreateDTO.setQuantity(12);
//            purchaseItemCreateDTO.setUnitPrice(BigDecimal.valueOf(125));
//
//            mockMvc.perform(post("/api/v1/purchaseItems/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchaseItemCreateDTO)))
//                    .andExpect(status().isConflict())
//                    .andDo(print());
//
//        }
//
//    }
//
//    @Nested
//    class PurchaseItemFetchTest {
//        Role role;
//        User user;
//        Category category;
//        Product product;
//        ProductBatch productBatch;
//        Supplier supplier;
//        Purchase purchase;
//        Purchase purchase2;
//        PurchaseItem purchaseItem;
//        PurchaseItem purchaseItem2;
//
//
//
//        @BeforeEach
//        void setup() {
//            purchaseItemRepository.deleteAll();
//            purchaseRepository.deleteAll();
//            supplierRepository.deleteAll();
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            role = new Role();
//            role.setName("Admin");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            user.setRole(role);
//            userRepository.save(user);
//
//
//            category = new Category();
//            category.setName("Pain Killer");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Advil");
//            product.setCategory(category);
//            product.setBrand("Vulcasil");
//            product.setBarcode("1234xyz");
//            product.setDescription("For pain relief");
//            product.setCreatedBy(user);
//            productRepository.save(product);
//
//            productBatch = new ProductBatch();
//            productBatch.setProduct(product);
//            productBatch.setLocation("Gensan");
//            productBatch.setExpiryDate(LocalDate.of(2029, 8, 19));
//            productBatch.setManufacturingDate(LocalDate.now());
//            productBatch.setQuantity(120);
//            productBatch.setBatchNumber("xyz1234");
//            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(32));
//            productBatch.setCreatedBy(user);
//            productBatchRepository.save(productBatch);
//
//            supplier = new Supplier();
//            supplier.setContactPerson("James");
//            supplier.setPhoneNumber("j0923432");
//            supplier.setName("TGP");
//            supplier.setEmail("tgp@gmail.com");
//            supplier.setAddressCity("M'lang");
//            supplier.setAddressState("Cotabato");
//            supplier.setAddressStreet("Garcia Street");
//            supplier.setAddressZipCode("9402");
//            supplier.setCreatedBy(user);
//            supplierRepository.save(supplier);
//
//            purchase = new Purchase();
//            purchase.setSupplier(supplier);
//            purchase.setPurchaseDate(LocalDate.now());
//            purchase.setPurchaseStatus(PurchaseStatus.RECEIVED);
//            purchase.setTotalAmount(BigDecimal.valueOf(3200));
//            purchase.setCreatedBy(user);
//
//            purchase2 = new Purchase();
//            purchase2.setSupplier(supplier);
//            purchase2.setPurchaseDate(LocalDate.now());
//            purchase2.setPurchaseStatus(PurchaseStatus.RECEIVED);
//            purchase2.setTotalAmount(BigDecimal.valueOf(3200));
//            purchase2.setCreatedBy(user);
//            purchaseRepository.saveAll(List.of(purchase, purchase2));
//
//            purchaseItem = new PurchaseItem();
//            purchaseItem.setProductBatch(productBatch);
//            purchaseItem.setPurchase(purchase);
//            purchaseItem.setQuantity(32);
//            purchaseItem.setUnitPrice(BigDecimal.valueOf(100));
//
//            purchaseItem2 = new PurchaseItem();
//            purchaseItem2.setProductBatch(productBatch);
//            purchaseItem2.setPurchase(purchase2);
//            purchaseItem2.setQuantity(32);
//            purchaseItem2.setUnitPrice(BigDecimal.valueOf(100));
//            purchaseItemRepository.saveAll(List.of(purchaseItem, purchaseItem2));
//
//        }
//
//        @DisplayName("Should retrieve all purchase items")
//        @Test
//        void shouldReturnSuccessfulRequest_fetchAllPurchaseItem() throws Exception {
//            mockMvc.perform(get("/api/v1/purchaseItems").contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().is2xxSuccessful())
//                    .andExpect(jsonPath("$.length()").value(2))
//                    .andDo(print());
//
//
//        }
//
//        @DisplayName("Should fetch specific purchase item")
//        @Test
//        void shouldReturnSuccessfulRequest_fetchSpecificPurchaseItem() throws Exception {
//            Long purchaseItemId = purchaseItem.getId();
//
//            mockMvc.perform(get("/api/v1/purchaseItems/" + purchaseItemId).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().is2xxSuccessful())
//                    .andExpect(jsonPath("$.purchaseItemId").value(purchaseItemId))
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail since the id is invalid or doesn't exist")
//        @Test
//        void shouldReturnNotFoundRequest_invalidPurchaseItemId() throws Exception {
//            Long nonExistentId = 99L;
//
//            mockMvc.perform(get("/api/v1/purchaseItems/" + nonExistentId).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isNotFound())
//                    .andDo(print());
//
//
//        }
//
//    }
//
//
//    @Nested
//    class PurchaseItemUpdateTest {
//
//        Role role;
//        User user;
//        Category category;
//        Product product;
//        ProductBatch productBatch;
//        Supplier supplier;
//        Purchase purchase;
//        Purchase purchase2;
//        PurchaseItem purchaseItem;
//
//
//        @BeforeEach
//        void setup() {
//            purchaseItemRepository.deleteAll();
//            purchaseRepository.deleteAll();
//            supplierRepository.deleteAll();
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            role = new Role();
//            role.setName("Admin");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            user.setRole(role);
//            userRepository.save(user);
//
//
//            category = new Category();
//            category.setName("Pain Killer");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Advil");
//            product.setCategory(category);
//            product.setBrand("Vulcasil");
//            product.setBarcode("1234xyz");
//            product.setDescription("For pain relief");
//            product.setCreatedBy(user);
//            productRepository.save(product);
//
//            productBatch = new ProductBatch();
//            productBatch.setProduct(product);
//            productBatch.setLocation("Gensan");
//            productBatch.setExpiryDate(LocalDate.of(2029,8,19));
//            productBatch.setManufacturingDate(LocalDate.now());
//            productBatch.setQuantity(120);
//            productBatch.setBatchNumber("xyz1234");
//            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(32));
//            productBatch.setCreatedBy(user);
//            productBatchRepository.save(productBatch);
//
//            supplier = new Supplier();
//            supplier.setContactPerson("James");
//            supplier.setPhoneNumber("j0923432");
//            supplier.setName("TGP");
//            supplier.setEmail("tgp@gmail.com");
//            supplier.setAddressCity("M'lang");
//            supplier.setAddressState("Cotabato");
//            supplier.setAddressStreet("Garcia Street");
//            supplier.setAddressZipCode("9402");
//            supplier.setCreatedBy(user);
//            supplierRepository.save(supplier);
//
//            purchase = new Purchase();
//            purchase.setSupplier(supplier);
//            purchase.setPurchaseDate(LocalDate.now());
//            purchase.setPurchaseStatus(PurchaseStatus.RECEIVED);
//            purchase.setTotalAmount(BigDecimal.valueOf(3200));
//            purchase.setCreatedBy(user);
//
//            purchase2 = new Purchase();
//            purchase2.setSupplier(supplier);
//            purchase2.setPurchaseDate(LocalDate.now());
//            purchase2.setPurchaseStatus(PurchaseStatus.RECEIVED);
//            purchase2.setTotalAmount(BigDecimal.valueOf(3200));
//            purchase2.setCreatedBy(user);
//            purchaseRepository.saveAll(List.of(purchase, purchase2));
//
//            purchaseItem =  new PurchaseItem();
//            purchaseItem.setProductBatch(productBatch);
//            purchaseItem.setPurchase(purchase);
//            purchaseItem.setQuantity(32);
//            purchaseItem.setUnitPrice(BigDecimal.valueOf(100));
//            purchaseItemRepository.save(purchaseItem);
//        }
//
//        @DisplayName("Should update the purchase item")
//        @Test
//        void shouldReturnSuccessfulRequest_updatePurchaseItem() throws Exception {
//            Long purchaseItemId = purchaseItem.getId();
//
//            PurchaseItemUpdateDTO purchaseItemUpdateDTO = new PurchaseItemUpdateDTO();
//            purchaseItemUpdateDTO.setQuantity(12);
//            purchaseItemUpdateDTO.setUnitPrice(BigDecimal.valueOf(10.5));
//
//            mockMvc.perform(put("/api/v1/purchaseItems/" + purchaseItemId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchaseItemUpdateDTO)))
//                    .andExpect(status().is2xxSuccessful())
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when you provide invalid or non existing purchase item id")
//        @Test
//        void shouldReturnNotFoundRequest_invalidPurchaseItemId() throws Exception {
//            Long nonExistentPurchaseItemId = 99L;
//
//            PurchaseItemUpdateDTO purchaseItemUpdateDTO = new PurchaseItemUpdateDTO();
//            purchaseItemUpdateDTO.setQuantity(12);
//            purchaseItemUpdateDTO.setUnitPrice(BigDecimal.valueOf(10.5));
//
//            mockMvc.perform(put("/api/v1/purchaseItems/" + nonExistentPurchaseItemId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchaseItemUpdateDTO)))
//                    .andExpect(status().isNotFound())
//                    .andDo(print());
//
//
//        }
//
//    }
//
//    @Nested
//    class PurchaseItemDeleteTest {
//        Role role;
//        User user;
//        Category category;
//        Product product;
//        ProductBatch productBatch;
//        Supplier supplier;
//        Purchase purchase;
//        Purchase purchase2;
//        PurchaseItem purchaseItem;
//
//
//        @BeforeEach
//        void setup() {
//            purchaseItemRepository.deleteAll();
//            purchaseRepository.deleteAll();
//            supplierRepository.deleteAll();
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            role = new Role();
//            role.setName("Admin");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            user.setRole(role);
//            userRepository.save(user);
//
//
//            category = new Category();
//            category.setName("Pain Killer");
//            categoryRepository.save(category);
//
//            product = new Product();
//            product.setName("Advil");
//            product.setCategory(category);
//            product.setBrand("Vulcasil");
//            product.setBarcode("1234xyz");
//            product.setDescription("For pain relief");
//            product.setCreatedBy(user);
//            productRepository.save(product);
//
//            productBatch = new ProductBatch();
//            productBatch.setProduct(product);
//            productBatch.setLocation("Gensan");
//            productBatch.setExpiryDate(LocalDate.of(2029, 8, 19));
//            productBatch.setManufacturingDate(LocalDate.now());
//            productBatch.setQuantity(120);
//            productBatch.setBatchNumber("xyz1234");
//            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(32));
//            productBatch.setCreatedBy(user);
//            productBatchRepository.save(productBatch);
//
//            supplier = new Supplier();
//            supplier.setContactPerson("James");
//            supplier.setPhoneNumber("j0923432");
//            supplier.setName("TGP");
//            supplier.setEmail("tgp@gmail.com");
//            supplier.setAddressCity("M'lang");
//            supplier.setAddressState("Cotabato");
//            supplier.setAddressStreet("Garcia Street");
//            supplier.setAddressZipCode("9402");
//            supplier.setCreatedBy(user);
//            supplierRepository.save(supplier);
//
//            purchase = new Purchase();
//            purchase.setSupplier(supplier);
//            purchase.setPurchaseDate(LocalDate.now());
//            purchase.setPurchaseStatus(PurchaseStatus.RECEIVED);
//            purchase.setTotalAmount(BigDecimal.valueOf(3200));
//            purchase.setCreatedBy(user);
//
//            purchase2 = new Purchase();
//            purchase2.setSupplier(supplier);
//            purchase2.setPurchaseDate(LocalDate.now());
//            purchase2.setPurchaseStatus(PurchaseStatus.RECEIVED);
//            purchase2.setTotalAmount(BigDecimal.valueOf(3200));
//            purchase2.setCreatedBy(user);
//            purchaseRepository.saveAll(List.of(purchase, purchase2));
//
//            purchaseItem = new PurchaseItem();
//            purchaseItem.setProductBatch(productBatch);
//            purchaseItem.setPurchase(purchase);
//            purchaseItem.setQuantity(32);
//            purchaseItem.setUnitPrice(BigDecimal.valueOf(100));
//            purchaseItemRepository.save(purchaseItem);
//        }
//
//        @DisplayName("Should delete purchase item")
//        @Test
//        void shouldReturnSuccessfulRequest_deletePurchaseItem() throws Exception {
//            Long purchaseItemId = purchaseItem.getId();
//
//
//            mockMvc.perform(delete("/api/v1/purchaseItems/" + purchaseItemId).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().is2xxSuccessful())
//                    .andExpect(content().string("Purchase item deleted successfully"))
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when the id is invalid")
//        @Test
//        void shouldReturnNotFoundRequest_invalidPurchaseItemId() throws Exception {
//            Long nonExistentPurchaseItemId = 99L;
//
//
//            mockMvc.perform(delete("/api/v1/purchaseItems/" + nonExistentPurchaseItemId).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isNotFound())
//                    .andDo(print());
//
//
//        }
//
//
//
//    }
//
//
//
//
//}