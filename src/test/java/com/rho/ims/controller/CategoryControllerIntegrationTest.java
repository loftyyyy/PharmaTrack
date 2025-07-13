package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.CategoryDTO;
import com.rho.ims.model.Category;
import com.rho.ims.respository.CategoryRepository;
import com.rho.ims.service.CategoryService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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


    @Nested
    class CreateCategoryTest{

        @DisplayName("Should create a new category")
        @Test
        void shouldReturnSuccessfulRequest_createCategory() throws Exception{
            CategoryDTO category = new CategoryDTO();
            category.setName("Test Category");

            mockmvc.perform(post("/api/v1/categories/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(category)))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());

        }

        @DisplayName("Should fail when name is blank")
        @Test
        void shouldReturnBadRequest_blankName() throws Exception{
            CategoryDTO category = new CategoryDTO();
            category.setName("");

            mockmvc.perform(post("/api/v1/categories/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(category)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors['name']").value("Name is required"))
                    .andDo(print());




        }






    }



}