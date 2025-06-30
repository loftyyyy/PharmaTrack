package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.LoginDTO;
import com.rho.ims.dto.SignupDTO;
import com.rho.ims.dto.UpdateUserDTO;
import com.rho.ims.model.Role;
import com.rho.ims.model.User;
import com.rho.ims.respository.RoleRepository;
import com.rho.ims.respository.UserRepository;
import com.rho.ims.service.RoleService;
import com.rho.ims.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import java.sql.Connection;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DataSource dataSource;

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





    @Nested
    class CreateUserTest {

        long testRoleId;

        @BeforeEach
        void setup(){
            userRepository.deleteAll();
            roleRepository.deleteAll();

            Role testRole = new Role();
            testRole.setName("Test Role");

            roleRepository.save(testRole);

            testRoleId = testRole.getId();


        }

        @DisplayName("Should create a new user")
        @Test
        void shouldReturnSuccessfulRequest_createUser() throws Exception{
            SignupDTO signupDTO = new SignupDTO();
            signupDTO.setUsername(username);
            signupDTO.setEmail(email);
            signupDTO.setPassword(password);
            signupDTO.setRoleId(testRoleId);

            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().string("User created successfully"))
                    .andDo(print());

        }

        @DisplayName("Should fail when username is blank")
        @Test
        void shouldReturnBadRequest_usernameIsBlank() throws Exception{

            SignupDTO signupDTO = new SignupDTO();
            signupDTO.setUsername("");
            signupDTO.setEmail(email);
            signupDTO.setPassword(password);
            signupDTO.setRoleId(testRoleId);

            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.username").value("Username is required"))
                    .andDo(print());


        }

        @DisplayName("Should fail when email is blank")
        @Test
        void shouldReturnBadRequest_emailIsBlank() throws Exception {

            SignupDTO signupDTO = new SignupDTO();
            signupDTO.setUsername(username);
            signupDTO.setEmail("");
            signupDTO.setPassword(password);
            signupDTO.setRoleId(testRoleId);

            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").value("Email is required"))
                    .andDo(print());


        }

        @DisplayName("Should fail when email is invalid")
        @Test
        void shouldReturnBadRequest_emailIsInvalid() throws Exception {

            SignupDTO signupDTO = new SignupDTO();
            signupDTO.setUsername(username);
            signupDTO.setEmail("someInvalidEmail@.com");
            signupDTO.setPassword(password);
            signupDTO.setRoleId(testRoleId);

            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").value("Invalid email"))
                    .andDo(print());


        }

        @DisplayName("Should fail when password is blank")
        @Test
        void shouldReturnBadRequest_passwordIsBlank() throws Exception{

            SignupDTO signupDTO = new SignupDTO();
            signupDTO.setUsername(username);
            signupDTO.setEmail(email);
            signupDTO.setPassword("");
            signupDTO.setRoleId(testRoleId);

            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.password[0]").value("Password is required"))
                    .andExpect(jsonPath("$.password[1]").value("Password must be at least 8 characters"))
                    .andDo(print());


        }

        @DisplayName("Should fail when role is null")
        @Test
        void shouldReturnBadRequest_roleIsNull() throws Exception {

            SignupDTO signupDTO = new SignupDTO();
            signupDTO.setUsername(username);
            signupDTO.setEmail(email);
            signupDTO.setPassword(password);
            signupDTO.setRoleId(null);

            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.roleId").value("Role id is required"))
                    .andDo(print());



        }



    }

    @Nested
    class LogInUserTest {
        long testRoleId;

        @BeforeEach
        void setup(){
            userRepository.deleteAll();
            roleRepository.deleteAll();

            Role testRole = new Role();
            testRole.setName("Test Role");

            User testUser = new User();
            testUser.setUsername(username);
            testUser.setPassword(password);
            testUser.setEmail(email);
            testUser.setRole(testRole);
            testRoleId = testUser.getId();

            userRepository.save(testUser);


        }

    }

    @Nested
    class GetUserTest {
        long testRoleId;

        @BeforeEach
        void setup(){
            userRepository.deleteAll();
            roleRepository.deleteAll();

            Role testRole = new Role();
            testRole.setName("Test Role");

            User testUser = new User();
            testUser.setUsername(username);
            testUser.setPassword(password);
            testUser.setEmail(email);
            testUser.setRole(testRole);
            testRoleId = testUser.getId();

            userRepository.save(testUser);


        }

    }

    @Nested
    class UpdateUserTest {
        long testRoleId;

        @BeforeEach
        void setup(){
            userRepository.deleteAll();
            roleRepository.deleteAll();

            Role testRole = new Role();
            testRole.setName("Test Role");

            User testUser = new User();
            testUser.setUsername(username);
            testUser.setPassword(password);
            testUser.setEmail(email);
            testUser.setRole(testRole);
            testRoleId = testUser.getId();

            userRepository.save(testUser);


        }

    }

    @Nested
    class DeleteUserTest {
        long testRoleId;

        @BeforeEach
        void setup(){
            userRepository.deleteAll();
            roleRepository.deleteAll();

            Role testRole = new Role();
            testRole.setName("Test Role");

            User testUser = new User();
            testUser.setUsername(username);
            testUser.setPassword(password);
            testUser.setEmail(email);
            testUser.setRole(testRole);
            testRoleId = testUser.getId();

            userRepository.save(testUser);


        }

    }

}