package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.CategoryCreateDTO;
import com.rho.ims.dto.CategoryUpdateDTO;
import com.rho.ims.model.Category;
import com.rho.ims.respository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    @Nested
    class FetchCategory {
        Category category1;
        Category category2;

        @BeforeEach
        void setup() {
            this.category1 = new Category();
            category1.setName("Category 1");

            this.category2 = new Category();
            category2.setName("Category 2");

            categoryRepository.saveAll(List.of(this.category1, this.category2));


        }

        @DisplayName("Should return all categories")
        @Test
        void shouldReturnSuccessfulRequest_retrievesAllCategories() throws Exception {

            mockmvc.perform(get("/api/v1/categories").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andDo(print());
        }

        @DisplayName("Should return no categories")
        @Test
        void shouldReturnSuccessfulRequest_emptyList() throws Exception {
            categoryRepository.deleteAll();

            mockmvc.perform(get("/api/v1/categories").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.length()").value(0))
                    .andDo(print());


        }

        // TODO: Reimplement this

//        @DisplayName("Should return unauthorized access")
//        @Test
//        void shouldReturnUnauthorizedRequest_401() throws Exception {
//            mockmvc.perform(get("/api/v1/categories").contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isUnauthorized())
//                    .andDo(print());
//
//        }




    }

    @Nested
    class CreateCategoryTest {

        @BeforeEach
        void setup(){
            categoryRepository.deleteAll();

            Category category = new Category();
            category.setName("Test Category");
            categoryRepository.save(category);

        }

        @DisplayName("Should create a new category")
        @Test
        void shouldReturnSuccessfulRequest_createCategory() throws Exception{
            categoryRepository.deleteAll();
            CategoryCreateDTO category = new CategoryCreateDTO();
            category.setName("Test Category");

            mockmvc.perform(post("/api/v1/categories/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(category)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }

        @DisplayName("Should fail when name is blank")
        @Test
        void shouldReturnBadRequest_blankName() throws Exception{
            CategoryCreateDTO category = new CategoryCreateDTO();
            category.setName("");

            mockmvc.perform(post("/api/v1/categories/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(category)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors['name']").value("Name is required"))
                    .andDo(print());
        }

        @DisplayName("Should fail when category name is already taken")
        @Test
        void shouldReturnConflictRequest_takenName() throws Exception {

            CategoryCreateDTO category = new CategoryCreateDTO();
            category.setName("Test Category");

            mockmvc.perform(post("/api/v1/categories/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(category)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("name already exists"))
                    .andDo(print());


        }






    }

    @Nested
    class UpdateCategoryTest {
        Category category;


        @BeforeEach
        void setup(){
            categoryRepository.deleteAll();

            category = new Category();
            category.setName("Test Category");

            categoryRepository.save(category);


        }

        @DisplayName("Should update the category")
        @Test
        void shouldReturnSuccessfulRequest_updateCategory() throws Exception {
            CategoryUpdateDTO categoryDTO = new CategoryUpdateDTO();
            categoryDTO.setName("New Name");


            mockMvc.perform(put("/api/v1/categories/" + this.category.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(categoryDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }



        @DisplayName("Should fail when there's already an existing name")
        @Test
        void shouldReturnConflictRequest_categoryNameAlreadyExist() throws Exception {
            Category category1 = new Category();
            category1.setName("SomeCategoryName");
            categoryRepository.save(category1);

            CategoryUpdateDTO categoryUpdateDTO = new CategoryUpdateDTO();
            categoryUpdateDTO.setName(category.getName());


            mockMvc.perform(put("/api/v1/categories/" +  category1.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(categoryUpdateDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Name already exists"))
                    .andDo(print());

        }


    }

    @Nested
    class CategoryDeleteTest {
        Category category;

        @BeforeEach
        void setup(){
            categoryRepository.deleteAll();

            category = new Category();
            category.setName("Test Category");

            categoryRepository.save(category);




        }

        @DisplayName("Should delete user")
        @Test
        void shouldReturnSuccessfulRequest_deleteCategory() throws Exception {

            mockMvc.perform(delete("/api/v1/categories/" + category.getId()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().string("Successfully deleted category"))
                    .andDo(print());


        }

        @DisplayName("Should fail when id doesn't exist")
        @Test
        void shouldReturnBadRequest_invalidId() throws Exception {

            long id = 999;

            mockMvc.perform(delete("/api/v1/categories/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Category doesn't exist"))
                    .andDo(print());



        }



    }




}