package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.product.ProductCreateDTO;
import com.rho.ims.dto.product.ProductUpdateDTO;
import com.rho.ims.model.Category;
import com.rho.ims.model.Product;
import com.rho.ims.model.Role;
import com.rho.ims.model.User;
import com.rho.ims.respository.CategoryRepository;
import com.rho.ims.respository.ProductRepository;
import com.rho.ims.respository.RoleRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;


    @Value("${test.user.username}")
    private String username;

    @Value("${test.user.email}")
    private String email;

    @Value("${test.user.password}")
    private String password;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Nested
    class FetchProductTest {

        Role staff;
        User user;
        Category vitamin;
        Product product1;
        Product product2;

        @BeforeEach
        void setup(){
            productRepository.deleteAll();
            categoryRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();

            staff = new Role();
            staff.setName("Staff");
            roleRepository.save(staff);

            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(staff);
            userRepository.save(user);


            vitamin = new Category();
            vitamin.setName("Vitamins");
            categoryRepository.save(vitamin);

            product1 = new Product();
            product1.setName("B12");
            product1.setBrand("Generic");
            product1.setDescription("");
            product1.setCategory(vitamin);
            product1.setBarcode("1234ABC");
            product1.setCreatedBy(user);

            product2 = new Product();
            product2.setName("B12");
            product2.setBrand("Generic");
            product2.setDescription("");
            product2.setCategory(vitamin);
            product2.setBarcode("1234ABCDEFG");
            product2.setCreatedBy(user);


            productRepository.saveAll(List.of(product1, product2));

        }


        @DisplayName("Should return all products")
        @Test
        void shouldReturnSuccessfulRequest_fetchAllProducts() throws Exception {

            mockMvc.perform(get("/api/v1/products").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andDo(print());

        }

        @DisplayName("Should return specific product")
        @Test
        void shouldReturnSuccessfulRequest_fetchSpecificProduct() throws Exception {
            Long id = product2.getId();

            mockMvc.perform(get("/api/v1/products/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.id").value(id))
                    .andDo(print());

        }

        @DisplayName("Should fail when product doesn't exist")
        @Test
        void shouldReturnNotFoundRequest_fetchInvalidProduct() throws Exception {
            Long id = 99L;

            mockMvc.perform(get("/api/v1/products/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());

        }


    }

    @Nested
    class CreateProductTest {
        Role staff;
        User user;
        Category vitamin;

        @BeforeEach
        void setup(){
            productRepository.deleteAll();
            categoryRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();

            vitamin = new Category();
            vitamin.setName("Vitamins");
            categoryRepository.save(vitamin);

            staff = new Role();
            staff.setName("Staff");
            roleRepository.save(staff);

            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(staff);
            userRepository.save(user);

        }

        @DisplayName("Should create product")
        @WithMockUser(username = "user", roles = "Staff")
        @Test
        void shouldReturnSuccessfulRequest_createProduct() throws Exception {
            ProductCreateDTO productCreateDTO = new ProductCreateDTO();
            productCreateDTO.setName("B12");
            productCreateDTO.setBrand("Generic");
            productCreateDTO.setBarcode("1234ABC");
            productCreateDTO.setCategoryId(vitamin.getId());
            productCreateDTO.setDescription("");

            mockMvc.perform(post("/api/v1/products/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productCreateDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }

        @DisplayName("Should fail when field/s is/are missing")
        @WithMockUser(username = "user", roles = "Staff")
        @Test
        void shouldReturnBadRequest_missingFields() throws Exception {
            ProductCreateDTO productCreateDTO = new ProductCreateDTO();
            productCreateDTO.setName("");
            productCreateDTO.setBrand("");
            productCreateDTO.setBarcode("1234ABC");
            productCreateDTO.setCategoryId(vitamin.getId());
            productCreateDTO.setDescription("");

            mockMvc.perform(post("/api/v1/products/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productCreateDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("fieldErrors['name']").value("Name is required"))
                    .andExpect(jsonPath("fieldErrors['brand']").value("Brand is required"))
                    .andDo(print());

        }
    }

    @Nested
    class UpdateProductTest {
        Category vitamin;
        Role staff;
        User user;
        Product vb12;


        @BeforeEach
        void setup(){
            productRepository.deleteAll();
            categoryRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();

            vitamin = new Category();
            vitamin.setName("Vitamins");
            categoryRepository.save(vitamin);

            staff = new Role();
            staff.setName("Staff");
            roleRepository.save(staff);

            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(staff);
            userRepository.save(user);

            vb12 =  new Product();
            vb12.setName("B12");
            vb12.setBrand("Genericzasdf");
            vb12.setBarcode("1234ABC");
            vb12.setCategory(vitamin);
            vb12.setDescription("");
            vb12.setCreatedBy(user);
            productRepository.save(vb12);

        }

        @DisplayName("Should update the product")
        @WithMockUser(username = "user", roles = "Staff")
        @Test
        void shouldReturnSuccessfulRequest_updateProduct() throws Exception {
            ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO();
            productUpdateDTO.setName("B12");
            productUpdateDTO.setBrand("Generic");
            productUpdateDTO.setBarcode("1234ABC");
            productUpdateDTO.setCategoryId(vitamin.getId());
            productUpdateDTO.setDescription("");

            Long id = this.vb12.getId();

            mockMvc.perform(put("/api/v1/products/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productUpdateDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }

        @DisplayName("Should fail when field/s are missing")
        @WithMockUser(username = "user", roles = "Staff")
        @Test
        void shouldReturnBadRequest_missingFields() throws Exception {
            ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO();
            productUpdateDTO.setName("");
            productUpdateDTO.setBrand("Generic");
            productUpdateDTO.setBarcode("");
            productUpdateDTO.setCategoryId(vitamin.getId());
            productUpdateDTO.setDescription("");

            Long id = this.vb12.getId();

            mockMvc.perform(put("/api/v1/products/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productUpdateDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());

        }



    }

    @Nested
    class DeleteProductTest {
        Category vitamin;
        Role staff;
        User user;
        Product vb12;


        @BeforeEach
        void setup(){
            productRepository.deleteAll();
            categoryRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();

            vitamin = new Category();
            vitamin.setName("Vitamins");
            categoryRepository.save(vitamin);

            staff = new Role();
            staff.setName("Staff");
            roleRepository.save(staff);

            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(staff);
            userRepository.save(user);

            vb12 =  new Product();
            vb12.setName("B12");
            vb12.setBrand("Genericzasdf");
            vb12.setBarcode("1234ABC");
            vb12.setCategory(vitamin);
            vb12.setDescription("");
            vb12.setCreatedBy(user);
            productRepository.save(vb12);

        }

        @DisplayName("Should delete product")
        @Test
        void shouldReturnSuccessfulRequest_deleteProduct() throws Exception {
            Long id = vb12.getId();

            mockMvc.perform(delete("/api/v1/products/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().string("Product deleted successfully"))
                    .andDo(print());

        }

        @DisplayName("Should fail when product doesn't eixist")
        @Test
        void shouldReturnNotFoundRequest_invalidProduct() throws Exception {
            Long id = 99L;

            mockMvc.perform(delete("/api/v1/products/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("message").value("product not found"))
                    .andDo(print());


        }

    }

}