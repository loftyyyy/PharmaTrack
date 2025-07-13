package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.UpdateRoleDTO;
import com.rho.ims.model.Role;
import com.rho.ims.respository.RoleRepository;
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
            roleRepository.deleteAll();
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
                    .andExpect(jsonPath("$.fieldErrors['name']").value("Name cannot be blank!"))
                    .andDo(print());

        }




    }

    @Nested
    class DeleteRoleTest {
        long testRoleId;

        @BeforeEach
        void setup(){

            Role testRole = new Role();
            testRole.setName("Test Role");
            roleRepository.save(testRole);

            testRoleId = testRole.getId();



        }

        @DisplayName("Should delete role")
        @Test
        void shouldReturnSuccessfulRequest_deleteRole() throws Exception {

            mockMvc.perform(delete("/api/v1/roles/" + testRoleId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().string("Role deleted successfully"))
                    .andDo(print());

        }

        @DisplayName("Should fail when passing invalid id that doesn't exist")
        @Test
        void shouldReturnBadRequest_invalidRole() throws Exception {
            long invalidId = 99;

            mockMvc.perform(delete("/api/v1/roles/" + invalidId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Error deleting role: Role not found with id: " + invalidId))
                    .andDo(print());

        }

    }

    @Nested
    class GetRoleTest {
        Role testRole;


        @BeforeEach
        void setup() {
            Role testRole = new Role();
            testRole.setName("Test Role");

            roleRepository.save(testRole);
            this.testRole = testRole;

        }

        @DisplayName("Should return role")
        @Test
        void shouldReturnSuccessfulRequest_retrieveRole() throws Exception {

            mockMvc.perform(get("/api/v1/roles/" + testRole.getId()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.id").value(testRole.getId()))
                    .andExpect(jsonPath("$.name").value(testRole.getName()))
                    .andDo(print());

        }

        @DisplayName("Should fail when id is invalid")
        @Test
        void shouldReturnBadRequest_invalidRole() throws Exception {
            long invalidId = 999;

            mockMvc.perform(get("/api/v1/roles/" + invalidId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Error retrieving role: Role not found with id: " + invalidId) )
                    .andDo(print());

        }



    }


    @Nested
    class UpdateRoleTest {
        long testRoleId;

        @BeforeEach
        void setup(){
            Role testRole = new Role();
            testRole.setName("Test Role");
            roleRepository.save(testRole);

            testRoleId = testRole.getId();

        }



        @DisplayName("Should update role")
        @Test
        void shouldReturnSuccessfulRequest_updateRole() throws Exception {
            UpdateRoleDTO updateRoleDTO = new UpdateRoleDTO();
            updateRoleDTO.setName("New Test Role");

            mockMvc.perform(put("/api/v1/roles/" + testRoleId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateRoleDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().string("Role Updated Successfully"))
                    .andDo(print());
        }

        @DisplayName("Should fail when invalid role id")
        @Test
        void shouldReturnBadRequest_updateRoleFail() throws Exception {
            long invalidId = 999;
            UpdateRoleDTO updateRoleDTO = new UpdateRoleDTO();
            updateRoleDTO.setName("New Role Name");

            mockMvc.perform(put("/api/v1/roles/" + invalidId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateRoleDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Error updating role: Role not found with id: " + invalidId))
                    .andDo(print());
        }

    }
}