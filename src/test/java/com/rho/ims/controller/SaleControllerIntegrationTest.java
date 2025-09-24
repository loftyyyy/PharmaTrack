//package com.rho.ims.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rho.ims.config.SecurityConfig;
//import com.rho.ims.dto.SaleCreateDTO;
//import com.rho.ims.dto.SaleItemCreateDTO;
//import com.rho.ims.dto.SaleVoidDTO;
//import com.rho.ims.enums.PaymentMethod;
//import com.rho.ims.enums.SaleStatus;
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
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
//@WithMockUser(username = "user", roles = "Staff")
//class SaleControllerIntegrationTest {
//
//    @Autowired
//    private CustomerRepository customerRepository;
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
//    private SaleRepository saleRepository;
//    @Autowired
//    private SaleItemRepository saleItemRepository;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private InventoryLogRepository inventoryLogRepository;
//
//    @Nested
//    class CreateSaleTest {
//        Customer customer;
//        ProductBatch productBatch;
//        Product product;
//        User user;
//        Role role;
//        Category category;
//        LocalDate expiryDate;
//        LocalDate manufactureDate;
//        Sale sale;
//
//        @BeforeEach
//        void setup() {
//            inventoryLogRepository.deleteAll();
//            saleItemRepository.deleteAll();
//            saleRepository.deleteAll();
//            customerRepository.deleteAll();
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
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
//            productBatchRepository.save(productBatch);
//
//            customer = new Customer();
//            customer.setName("Melinda");
//            customer.setEmail("melinda@gmail.com");
//            customer.setPhoneNumber("09282714224");
//            customer.setAddressZipCode("9402");
//            customer.setAddressStreet("Garcia");
//            customer.setAddressState("Cotabato");
//            customerRepository.save(customer);
//        }
//
//        @DisplayName("Should create a sale")
//        @Test
//        void shouldReturnSuccessfulRequest_createSale() throws Exception {
//            SaleItemCreateDTO saleItem = new SaleItemCreateDTO();
//            saleItem.setProductBatchId(productBatch.getId());
//            saleItem.setProductId(product.getId());
//            saleItem.setQuantity(3);
//            saleItem.setUnitPrice(BigDecimal.valueOf(35));
//
//            SaleCreateDTO saleCreateDTO = new SaleCreateDTO();
//            saleCreateDTO.setCustomerId(customer.getId());
//            saleCreateDTO.setItems(List.of(saleItem));
//            saleCreateDTO.setPaymentMethod(PaymentMethod.CASH);
//            saleCreateDTO.setSaleDate(LocalDate.now());
//            saleCreateDTO.setDiscountAmount(BigDecimal.ZERO);
//
//            mockMvc.perform(post("/api/v1/sales/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(saleCreateDTO)))
//                    .andExpect(status().is2xxSuccessful())
//                    .andDo(print());
//
//        }
//
//
//
//        @DisplayName("Should fail when there are missing field/s")
//        @Test
//        void shouldReturnBadRequest_missingFields() throws Exception {
//            SaleItemCreateDTO saleItem = new SaleItemCreateDTO();
//            saleItem.setProductBatchId(productBatch.getId());
//            saleItem.setProductId(product.getId());
//            saleItem.setQuantity(3);
//            saleItem.setUnitPrice(BigDecimal.valueOf(35));
//
//            SaleCreateDTO saleCreateDTO = new SaleCreateDTO();
//            saleCreateDTO.setCustomerId(null);
//            saleCreateDTO.setItems(null);
//            saleCreateDTO.setPaymentMethod(null);
//            saleCreateDTO.setSaleDate(null);
//            saleCreateDTO.setDiscountAmount(null);
//
//            mockMvc.perform(post("/api/v1/sales/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(saleCreateDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andDo(print());
//        }
//
//        @DisplayName("Should give internal error when field/s on sale item are missing")
//        @Test
//        void shouldReturnInternalServerError_missingFieldsOnSaleItem() throws Exception {
//            SaleItemCreateDTO saleItem = new SaleItemCreateDTO();
//            saleItem.setProductBatchId(null);
//            saleItem.setProductId(null);
//            saleItem.setQuantity(null);
//            saleItem.setUnitPrice(BigDecimal.valueOf(35));
//
//
//            SaleCreateDTO saleCreateDTO = new SaleCreateDTO();
//            saleCreateDTO.setCustomerId(customer.getId());
//            saleCreateDTO.setItems(List.of(saleItem));
//            saleCreateDTO.setPaymentMethod(PaymentMethod.CASH);
//            saleCreateDTO.setSaleDate(LocalDate.now());
//            saleCreateDTO.setDiscountAmount(BigDecimal.ZERO);
//
//
//            mockMvc.perform(post("/api/v1/sales/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(saleCreateDTO)))
//                    .andExpect(status().isInternalServerError())
//                    .andDo(print());
//
//        }
//
//
//
//    }
//
//    @Nested
//    class FetchSaleTest {
//        Customer customer;
//        ProductBatch productBatch;
//        Product product;
//        User user;
//        Role role;
//        Category category;
//        LocalDate expiryDate;
//        LocalDate manufactureDate;
//        Sale sale;
//        SaleItem saleItem;
//
//        @BeforeEach
//        void setup() {
//            // clean db
//            inventoryLogRepository.deleteAll();
//            saleItemRepository.deleteAll();
//            saleItemRepository.deleteAll(); // delete children first
//            saleRepository.deleteAll();     // then parent
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            customerRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            // role + user
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
//            // category + product
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
//            // batch
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
//            productBatchRepository.save(productBatch);
//
//            // customer
//            customer = new Customer();
//            customer.setName("Melinda");
//            customer.setEmail("melinda@gmail.com");
//            customer.setPhoneNumber("09282714224");
//            customer.setAddressZipCode("9402");
//            customer.setAddressStreet("Garcia");
//            customer.setAddressState("Cotabato");
//            customerRepository.save(customer);
//
//            // sale + item
//            saleItem = new SaleItem();
//            saleItem.setProduct(product);
//            saleItem.setProductBatch(productBatch);
//            saleItem.setQuantity(2);
//            saleItem.setUnitPrice(BigDecimal.valueOf(50));
//            saleItem.setSubTotal(BigDecimal.valueOf(100));
//
//            sale = new Sale();
//            sale.setCustomer(customer);
//            sale.setSaleDate(LocalDate.now());
//            sale.setPaymentMethod(PaymentMethod.CASH);
//            sale.setDiscountAmount(BigDecimal.ZERO);
//            sale.setStatus(SaleStatus.PENDING);
//            sale.setCreatedBy(user);
//
//            sale.setSaleItems(List.of(saleItem));
//            sale.setTotalAmount(BigDecimal.valueOf(100));
//            sale.setTaxAmount(BigDecimal.valueOf(12));
//            sale.setGrandTotal(BigDecimal.valueOf(112));
//
//            // link item back to sale
//            saleItem.setSale(sale);
//
//            saleRepository.save(sale);
//        }
//
//        @Test
//        @DisplayName("Should fetch all sales")
//        void shouldFetchAllSales() throws Exception {
//            mockMvc.perform(get("/api/v1/sales"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$[0].saleId").value(sale.getId()))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("Should fetch sale by ID successfully")
//        void shouldFetchSaleById() throws Exception {
//            mockMvc.perform(get("/api/v1/sales/" + sale.getId()))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.saleId").value(sale.getId()))
//                    .andExpect(jsonPath("$.customerName").value("Melinda"))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("Should return 404 when sale not found")
//        void shouldReturnNotFoundWhenSaleDoesNotExist() throws Exception {
//            mockMvc.perform(get("/api/v1/sales/9999"))
//                    .andExpect(status().isNotFound())
//                    .andDo(print());
//        }
//
//
//        @Test
//        @DisplayName("Should fetch sale with items included")
//        void shouldFetchSaleWithItems() throws Exception {
//            mockMvc.perform(get("/api/v1/sales/" + sale.getId()))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.saleItems[0].productName").value("Paracetamol"))
//                    .andExpect(jsonPath("$.saleItems[0].quantity").value(2))
//                    .andDo(print());
//        }
//    }
//
//    @Nested
//    class ConfirmSaleTest {
//        Customer customer;
//        ProductBatch productBatch;
//        Product product;
//        User user;
//        Role role;
//        Category category;
//        Sale sale;
//
//        @BeforeEach
//        void setup() {
//            // delete in the right order (children first)
//            inventoryLogRepository.deleteAll();
//            saleItemRepository.deleteAll();
//            saleRepository.deleteAll();
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            customerRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            // create role & user
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
//            productBatch = new ProductBatch();
//            productBatch.setProduct(product);
//            productBatch.setBatchNumber("xy1234");
//            productBatch.setQuantity(10);
//            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(320));
//            productBatch.setLocation("Japan");
//            productBatch.setExpiryDate(LocalDate.of(2029, 6, 25));
//            productBatch.setManufacturingDate(LocalDate.of(2025, 8, 30));
//            productBatch.setCreatedBy(user);
//            productBatchRepository.save(productBatch);
//
//            customer = new Customer();
//            customer.setName("Melinda");
//            customer.setEmail("melinda@gmail.com");
//            customer.setPhoneNumber("09282714224");
//            customer.setAddressZipCode("9402");
//            customer.setAddressStreet("Garcia");
//            customer.setAddressState("Cotabato");
//            customerRepository.save(customer);
//
//            // create sale with item
//            SaleItem saleItem = new SaleItem();
//            saleItem.setProduct(product);
//            saleItem.setProductBatch(productBatch);
//            saleItem.setQuantity(2);
//            saleItem.setUnitPrice(BigDecimal.valueOf(35));
//            saleItem.setSubTotal(BigDecimal.valueOf(70));
//
//            sale = new Sale();
//            sale.setCustomer(customer);
//            sale.setSaleDate(LocalDate.now());
//            sale.setPaymentMethod(PaymentMethod.CASH);
//            sale.setDiscountAmount(BigDecimal.ZERO);
//            sale.setCreatedBy(user);
//            sale.setStatus(SaleStatus.PENDING);
//            sale.setSaleItems(List.of(saleItem));
//            saleItem.setSale(sale);
//
//            sale.setTotalAmount(BigDecimal.valueOf(70));
//            sale.setTaxAmount(BigDecimal.valueOf(70).multiply(BigDecimal.valueOf(0.12)));
//            sale.setGrandTotal(sale.getTotalAmount().add(sale.getTaxAmount()));
//
//            saleRepository.save(sale);
//        }
//
//        @Test
//        @DisplayName("Should confirm a pending sale and reduce batch stock")
//        void shouldConfirmSale() throws Exception {
//            mockMvc.perform(post("/api/v1/sales/" + sale.getId() + "/confirm"  ).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.saleStatus").value(String.valueOf(SaleStatus.CONFIRMED)))
//                    .andDo(print());
//
//            ProductBatch updatedBatch = productBatchRepository.findById(productBatch.getId()).orElseThrow();
//            assertThat(updatedBatch.getQuantity()).isEqualTo(8); // 10 - 2
//        }
//
//        @Test
//        @DisplayName("Should fail when sale not found")
//        void shouldFailWhenSaleNotFound() throws Exception {
//            mockMvc.perform(post("/api/v1/sales/9999/confirm")
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isNotFound());
//        }
//    }
//
//    @Nested
//    class VoidSaleTest {
//        Customer customer;
//        ProductBatch productBatch;
//        Product product;
//        User user;
//        Role role;
//        Category category;
//        Sale sale;
//
//        @BeforeEach
//        void setup() {
//            // delete in the right order (children first)
//            inventoryLogRepository.deleteAll();
//            saleItemRepository.deleteAll();
//            saleRepository.deleteAll();
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            customerRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            // create role & user
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
//            productBatch = new ProductBatch();
//            productBatch.setProduct(product);
//            productBatch.setBatchNumber("xy1234");
//            productBatch.setQuantity(10);
//            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(320));
//            productBatch.setLocation("Japan");
//            productBatch.setExpiryDate(LocalDate.of(2029, 6, 25));
//            productBatch.setManufacturingDate(LocalDate.of(2025, 8, 30));
//            productBatch.setCreatedBy(user);
//            productBatchRepository.save(productBatch);
//
//            customer = new Customer();
//            customer.setName("Melinda");
//            customer.setEmail("melinda@gmail.com");
//            customer.setPhoneNumber("09282714224");
//            customer.setAddressZipCode("9402");
//            customer.setAddressStreet("Garcia");
//            customer.setAddressState("Cotabato");
//            customerRepository.save(customer);
//
//            // create sale with item
//            SaleItem saleItem = new SaleItem();
//            saleItem.setProduct(product);
//            saleItem.setProductBatch(productBatch);
//            saleItem.setQuantity(2);
//            saleItem.setUnitPrice(BigDecimal.valueOf(35));
//            saleItem.setSubTotal(BigDecimal.valueOf(70));
//
//            sale = new Sale();
//            sale.setCustomer(customer);
//            sale.setSaleDate(LocalDate.now());
//            sale.setPaymentMethod(PaymentMethod.CASH);
//            sale.setDiscountAmount(BigDecimal.ZERO);
//            sale.setCreatedBy(user);
//            sale.setStatus(SaleStatus.CONFIRMED);
//            sale.setSaleItems(List.of(saleItem));
//            saleItem.setSale(sale);
//
//            sale.setTotalAmount(BigDecimal.valueOf(70));
//            sale.setTaxAmount(BigDecimal.valueOf(70).multiply(BigDecimal.valueOf(0.12)));
//            sale.setGrandTotal(sale.getTotalAmount().add(sale.getTaxAmount()));
//
//            saleRepository.save(sale);
//        }
//
//        @Test
//        @DisplayName("Should void a confirmed sale and restore stock")
//        void shouldVoidSale() throws Exception {
//            SaleVoidDTO dto = new SaleVoidDTO();
//            dto.setVoidReason("Customer returned items");
//
//            mockMvc.perform(post("/api/v1/sales/" + sale.getId() + "/void" ).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
//                    .andExpect(status().is2xxSuccessful());
//
//            Sale updated = saleRepository.findById(sale.getId()).orElseThrow();
//            assertThat(updated.getIsVoided()).isTrue();
//            assertThat(updated.getVoidReason()).isEqualTo("Customer returned items");
//
//            ProductBatch updatedBatch = productBatchRepository.findById(productBatch.getId()).orElseThrow();
//            assertThat(updatedBatch.getQuantity()).isEqualTo(12); // restored +2
//            System.out.println(updatedBatch.getQuantity());
//        }
//
//        @Test
//        @DisplayName("Should fail when sale is not confirmed")
//        void shouldFailWhenNotConfirmed() throws Exception {
//            sale.setStatus(SaleStatus.PENDING);
//            saleRepository.save(sale);
//
//            SaleVoidDTO dto = new SaleVoidDTO();
//            dto.setVoidReason("Not valid");
//
//            mockMvc.perform(post("/api/v1/sales/" + sale.getId() + "/void" ).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
//                    .andExpect(status().is5xxServerError())
//                    .andDo(print());
//        }
//
//
//    }
//
//    @Nested
//    class CancelSaleTest {
//        Customer customer;
//        ProductBatch productBatch;
//        Product product;
//        User user;
//        Role role;
//        Category category;
//        Sale sale;
//
//        @BeforeEach
//        void setup() {
//            // delete in the right order (children first)
//            inventoryLogRepository.deleteAll();
//            saleItemRepository.deleteAll();
//            saleRepository.deleteAll();
//            productBatchRepository.deleteAll();
//            productRepository.deleteAll();
//            categoryRepository.deleteAll();
//            customerRepository.deleteAll();
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            // create role & user
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
//            productBatch = new ProductBatch();
//            productBatch.setProduct(product);
//            productBatch.setBatchNumber("xy1234");
//            productBatch.setQuantity(10);
//            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(320));
//            productBatch.setLocation("Japan");
//            productBatch.setExpiryDate(LocalDate.of(2029, 6, 25));
//            productBatch.setManufacturingDate(LocalDate.of(2025, 8, 30));
//            productBatch.setCreatedBy(user);
//            productBatchRepository.save(productBatch);
//
//            customer = new Customer();
//            customer.setName("Melinda");
//            customer.setEmail("melinda@gmail.com");
//            customer.setPhoneNumber("09282714224");
//            customer.setAddressZipCode("9402");
//            customer.setAddressStreet("Garcia");
//            customer.setAddressState("Cotabato");
//            customerRepository.save(customer);
//
//            // create sale with item
//            SaleItem saleItem = new SaleItem();
//            saleItem.setProduct(product);
//            saleItem.setProductBatch(productBatch);
//            saleItem.setQuantity(2);
//            saleItem.setUnitPrice(BigDecimal.valueOf(35));
//            saleItem.setSubTotal(BigDecimal.valueOf(70));
//
//            sale = new Sale();
//            sale.setCustomer(customer);
//            sale.setSaleDate(LocalDate.now());
//            sale.setPaymentMethod(PaymentMethod.CASH);
//            sale.setDiscountAmount(BigDecimal.ZERO);
//            sale.setCreatedBy(user);
//            sale.setStatus(SaleStatus.PENDING);
//            sale.setSaleItems(List.of(saleItem));
//            saleItem.setSale(sale);
//
//            sale.setTotalAmount(BigDecimal.valueOf(70));
//            sale.setTaxAmount(BigDecimal.valueOf(70).multiply(BigDecimal.valueOf(0.12)));
//            sale.setGrandTotal(sale.getTotalAmount().add(sale.getTaxAmount()));
//
//            saleRepository.save(sale);
//        }
//
//        @Test
//        @DisplayName("Should cancel a pending sale")
//        void shouldCancelSale() throws Exception {
//            mockMvc.perform(post("/api/v1/sales/{id}/cancel", sale.getId()).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andDo(print());
//
//            Sale updated = saleRepository.findById(sale.getId()).orElseThrow();
//            assertThat(updated.getStatus()).isEqualTo(SaleStatus.CANCELLED);
//        }
//
//
//    }
//
//
//
//}