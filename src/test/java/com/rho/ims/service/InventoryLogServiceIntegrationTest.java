package com.rho.ims.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.InventoryLogCreateDTO;
import com.rho.ims.enums.ChangeType;
import com.rho.ims.enums.PaymentMethod;
import com.rho.ims.enums.PurchaseStatus;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class InventoryLogServiceIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;

    @Value("${test.user.username}")
    private String username;

    @Value("${test.user.email}")
    private String email;

    @Value("${test.user.password}")
    private String password;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductBatchRepository productBatchRepository;
    private SaleItemRepository saleItemRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private InventoryLogRepository inventoryLogRepository;

    @Nested
    class InventoryLogCreateTest {
        Role role;
        User user;
        Supplier supplier;
        Customer customer;
        Category category;
        Product product;
        Product product2;
        ProductBatch productBatch;
        ProductBatch productBatch2;
        Sale sale;
        Purchase purchase;
        InventoryLog inventoryLog;

        @BeforeEach
        void setup() {
            // ===== ROLE & USER =====
            role = new Role();
            role.setName("Staff");
            roleRepository.save(role);

            user = new User();
            user.setRole(role);
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            userRepository.save(user);

            // ===== CATEGORY =====
            category = new Category();
            category.setName("Pain Reliever");
            categoryRepository.save(category);

            // ===== CUSTOMER =====
            customer = new Customer();
            customer.setName("Melinda");
            customer.setEmail("melinda@gmail.com");
            customer.setPhoneNumber("09282714224");
            customer.setAddressZipCode("9402");
            customer.setAddressStreet("Garcia");
            customer.setAddressState("Cotabato");
            customerRepository.save(customer);

            // ===== SUPPLIER =====
            supplier = new Supplier();
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

            // ===== PRODUCTS =====
            product = new Product();
            product.setName("Advil");
            product.setCategory(category);
            product.setDescription("For pain relief");
            product.setBarcode("32xwe73");
            product.setBrand("Generic");
            product.setCreatedBy(user);

            product2 = new Product();
            product2.setName("Paracetamol");
            product2.setCategory(category);
            product2.setDescription("For headache relief");
            product2.setBarcode("32xwe7332");
            product2.setBrand("Generic");
            product2.setCreatedBy(user);

            productRepository.saveAll(List.of(product, product2));

            // ===== PURCHASE =====
            purchase = new Purchase();
            purchase.setSupplier(supplier);
            purchase.setTotalAmount(BigDecimal.valueOf(200.00));
            purchase.setPurchaseDate(LocalDate.now());
            purchase.setPurchaseStatus(PurchaseStatus.RECEIVED);
            purchase.setCreatedBy(user);
            purchaseRepository.save(purchase);

            // ===== PRODUCT BATCHES =====
            productBatch = new ProductBatch();
            productBatch.setProduct(product);
            productBatch.setBatchNumber("xyz1234");
            productBatch.setExpiryDate(LocalDate.of(2029, 8, 23));
            productBatch.setManufacturingDate(LocalDate.of(2025, 5, 23));
            productBatch.setLocation("");
            productBatch.setQuantity(10);
            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(12.5));
            productBatch.setCreatedBy(user);

            productBatch2 = new ProductBatch();
            productBatch2.setProduct(product2);
            productBatch2.setBatchNumber("1234xyz");
            productBatch2.setExpiryDate(LocalDate.of(2029, 8, 23));
            productBatch2.setManufacturingDate(LocalDate.of(2025, 5, 23));
            productBatch2.setLocation("");
            productBatch2.setQuantity(20);
            productBatch2.setPurchasePricePerUnit(BigDecimal.valueOf(5.5));
            productBatch2.setCreatedBy(user);

            productBatchRepository.saveAll(List.of(productBatch, productBatch2));

            // ===== SALE & SALE ITEMS =====
            sale = new Sale();
            sale.setCustomer(customer);
            sale.setPaymentMethod(PaymentMethod.CASH);
            sale.setSaleDate(LocalDate.now());
            sale.setCreatedBy(user);

            SaleItem saleItem = new SaleItem();
            saleItem.setProduct(product);
            saleItem.setProductBatch(productBatch);
            saleItem.setQuantity(3);
            saleItem.setUnitPrice(BigDecimal.valueOf(32.5));
            saleItem.setSale(sale);
            saleItem.setSubtotal(BigDecimal.valueOf(32.5).multiply(BigDecimal.valueOf(3)));

            SaleItem saleItem2 = new SaleItem();
            saleItem2.setProduct(product2);
            saleItem2.setProductBatch(productBatch2);
            saleItem2.setQuantity(6);
            saleItem2.setUnitPrice(BigDecimal.valueOf(15));
            saleItem2.setSale(sale);
            saleItem2.setSubtotal(BigDecimal.valueOf(15).multiply(BigDecimal.valueOf(6)));

            sale.setSaleItems(List.of(saleItem, saleItem2));
            sale.setTotalAmount(saleItem.getSubtotal().add(saleItem2.getSubtotal()));

            saleRepository.save(sale);

            // ===== INVENTORY LOG =====
            inventoryLog = new InventoryLog();
            inventoryLog.setProduct(product);
            inventoryLog.setProductBatch(productBatch);
            inventoryLog.setChangeType(ChangeType.OUT);
            inventoryLog.setQuantityChanged(3);
            inventoryLog.setSale(sale);
            inventoryLog.setPurchase(purchase);
            inventoryLog.setCreatedBy(user);
            inventoryLogRepository.save(inventoryLog);
        }

        @DisplayName("Should create inventory log")
        @Test
        void shouldReturnSuccessfulRequest_createInventoryLog() throws Exception {
            InventoryLogCreateDTO inventoryLogCreateDTO = new InventoryLogCreateDTO();
            inventoryLogCreateDTO.setProductBatchId(productBatch2.getId());
            inventoryLogCreateDTO.setProductId(product2.getId());
            inventoryLogCreateDTO.setSaleId(sale.getId());
            inventoryLogCreateDTO.setPurchaseId(purchase.getId());
            inventoryLogCreateDTO.setAdjustmentReference("");
            inventoryLogCreateDTO.setChangeType(ChangeType.OUT);
            inventoryLogCreateDTO.setQuantityChanged(2);
            inventoryLogCreateDTO.setReason("");

            mockMvc.perform(post("/api/v1/"));

        }

    }

    @Nested
    class InventoryLogFetchTest {

        @BeforeEach
        void setup() {

        }

    }










}