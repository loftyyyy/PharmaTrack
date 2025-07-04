package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.model.Role;
import com.rho.ims.respository.RoleRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class RoleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Nested
    class CreateRoleTest {

        @DisplayName("Should create a new role")
        @Test
        void shouldReturnSuccessRequest_createRole() throws Exception {
            Role newRole = new Role();
            newRole.setName("Test Role");

            mockMvc.perform(post("/api/v1/roles/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newRole)))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().string("Role created successfully: Test Role"))
                    .andDo(print());

        }

        @DisplayName("Should fail when role is null or blank")
        @Test
        void shouldReturnBadRequest_invalidRole() throws Exception {
            Role newRole = new Role();
            newRole.setName(null);

            mockMvc.perform(post("/api/v1/roles/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newRole)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").value("Name cannot be blank!"))
                    .andDo(print());






        }




    }


    @Test
    void createRole() {
    }

    @Test
    void deleteRole() {
    }

    @Test
    void getRole() {
    }

    @Test
    void updateRole() {
    }
}