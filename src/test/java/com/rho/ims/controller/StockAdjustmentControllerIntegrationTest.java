package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.StockAdjustmentCreateDTO;
import com.rho.ims.enums.ChangeType;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
class StockAdjustmentControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private StockAdjustmentRepository stockAdjustmentRepository;
    @Autowired
    private InventoryLogRepository inventoryLogRepository;
    @Autowired
    private SaleRepository saleRepository;
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
    private ObjectMapper objectMapper;

    @Nested
     class StockAdjustmentTest {
        User user;
        Role role;
        Category category;
        Product product;
        ProductBatch productBatch;

        @BeforeEach
        void setup() {
            // delete children first to avoid FK issues
            stockAdjustmentRepository.deleteAll();
            inventoryLogRepository.deleteAll();
            saleRepository.deleteAll();
            productBatchRepository.deleteAll();
            productRepository.deleteAll();
            categoryRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();

            // create role & user
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

            productBatch = new ProductBatch();
            productBatch.setProduct(product);
            productBatch.setBatchNumber("batch123");
            productBatch.setQuantity(10);
            productBatch.setPurchasePricePerUnit(BigDecimal.valueOf(100));
            productBatch.setLocation("Main");
            productBatch.setExpiryDate(LocalDate.of(2029, 6, 25));
            productBatch.setManufacturingDate(LocalDate.of(2025, 8, 30));
            productBatch.setCreatedBy(user);
            productBatchRepository.save(productBatch);
        }

        @Test
        @DisplayName("Should create a stock adjustment and log inventory")
        void shouldCreateStockAdjustment() throws Exception {
            StockAdjustmentCreateDTO dto = new StockAdjustmentCreateDTO();
            dto.setProductId(product.getId());
            dto.setProductBatchId(productBatch.getId());
            dto.setQuantityChanged(5);
            dto.setReason("Manual correction: found extra stock");

            mockMvc.perform(post("/api/v1/stockAdjustments/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

            // Verify adjustment was created
            List<StockAdjustment> adjustments = stockAdjustmentRepository.findAll();
            assertThat(adjustments).hasSize(1);
            StockAdjustment adjustment = adjustments.get(0);
            assertThat(adjustment.getQuantityChanged()).isEqualTo(5);
            assertThat(adjustment.getReason()).isEqualTo("Manual correction: found extra stock");

            // Verify batch was updated
            ProductBatch updatedBatch = productBatchRepository.findById(productBatch.getId()).orElseThrow();
            assertThat(updatedBatch.getQuantity()).isEqualTo(15);

            // Verify inventory log was created
            List<InventoryLog> logs = inventoryLogRepository.findAll();
            assertThat(logs).hasSize(1);
            InventoryLog log = logs.get(0);
            assertThat(log.getChangeType()).isEqualTo(ChangeType.ADJUST);
            assertThat(log.getQuantityChanged()).isEqualTo(5);
            assertThat(log.getReason()).contains("Manual correction");
        }
    }
}