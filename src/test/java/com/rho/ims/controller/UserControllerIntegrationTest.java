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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import java.sql.Connection;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

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
            roleRepository.save(testRole);

            User testUser = new User();
            testUser.setUsername(username);
            testUser.setPassword(passwordEncoder.encode(password));
            testUser.setEmail(email);
            testUser.setRole(testRole);
            userRepository.save(testUser);

            testRoleId = testRole.getId();
        }

        @DisplayName("Should Log in the user")
        @Test
        void shouldReturnSuccessfulRequest_LogIn() throws Exception {
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername(username);
            loginDTO.setPassword(password);

            mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().string("Login Successfully"))
                    .andDo(print());

        }

        @DisplayName("Should fail with wrong username")
        @Test
        void shouldReturnBadRequest_wrongUsername() throws Exception {
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername("SomeWrongUsername");
            loginDTO.setPassword(password);

            mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Login failed: Invalid username or password"))
                    .andDo(print());
        }

        @DisplayName("Should fail with wrong password")
        @Test
        void shouldReturnBadRequest_wrongPassword() throws Exception {


            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername(username);
            loginDTO.setPassword("someWrongPassword");

            mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Login failed: Invalid username or password"))
                    .andDo(print());



        }

        @DisplayName("Should fail with blank username")
        @Test
        void shouldReturnBadRequest_blankUsername() throws Exception{

            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername("");
            loginDTO.setPassword(password);

            mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.username[0]").value("Username is required"))
                    .andDo(print());





        }

        @DisplayName("Should fail with blank password")
        @Test
        void shouldReturnBadRequest_blankPassword() throws Exception{

            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername(username);
            loginDTO.setPassword("");

            mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.password[0]").value("Password is required"))
                    .andExpect(jsonPath("$.password[1]").value("Password must be at least 8 characters"))
                    .andDo(print());





        }

    }

    @Nested
    class GetUserTest {
        long testRoleId;
        long testUserId;

        @BeforeEach
        void setup(){
            userRepository.deleteAll();
            roleRepository.deleteAll();

            Role testRole = new Role();
            testRole.setName("Test Role");
            roleRepository.save(testRole);

            User testUser = new User();
            testUser.setUsername(username);
            testUser.setPassword(password);
            testUser.setEmail(email);
            testUser.setRole(testRole);
            userRepository.save(testUser);

            testRoleId = testRole.getId();
            testUserId = testUser.getId();


        }

        @DisplayName("Should return a user")
        @Test
        void shouldReturnSuccessfulRequest_getUser() throws Exception{
            String roleName = roleService.findById(testRoleId).getName();

            mockMvc.perform(get("/api/v1/users/" + testUserId + "").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.username").value(username))
                    .andExpect(jsonPath("$.email").value(email))
                    .andExpect(jsonPath("$.roleName").value(roleName))
                    .andDo(print());
        }

        @DisplayName("Should fail with invalid id format")
        @Test
        void shouldReturnBadRequest_invalidId() throws Exception{
            // TODO: Might implement a Controller Advice
            String id = "xyz";

            mockMvc.perform(get("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print());



        }

        @DisplayName("Should fail with id that doesn't exist")
        @Test
        void shouldReturnBadRequest_idNotExist() throws Exception {
            long id = 999;

            mockMvc.perform(get("/api/v1/users/" + id + "").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("User retrieval failed: User not found"))
                    .andDo(print());
        }



    }

    @Nested
    class UpdateUserTest {
        long testRoleId;
        long testUserId;

        @BeforeEach
        void setup(){
            userRepository.deleteAll();
            roleRepository.deleteAll();

            Role testRole = new Role();
            testRole.setName("Test Role");
            roleRepository.save(testRole);

            User testUser = new User();
            testUser.setUsername(username);
            testUser.setPassword(password);
            testUser.setEmail(email);
            testUser.setRole(testRole);

            userRepository.save(testUser);

            testRoleId = testUser.getId();
            testUserId = testUser.getId();

        }


        @DisplayName("Should update the user")
        @Test
        void shouldReturnSuccessfulRequest_updateUser() throws Exception {
            UpdateUserDTO updateUserDTO = new UpdateUserDTO();
            updateUserDTO.setUsername("someNewUsername");
            updateUserDTO.setEmail("someNewEmail@gmail.com");
            updateUserDTO.setPassword("someNewPassword");

            mockMvc.perform(put("/api/v1/users/" + testUserId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserDTO)))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().string("Successfully updated profile"))
                    .andDo(print());


        }


        @DisplayName("Should fail when username is missing")
        @Test
        void shouldReturnBadRequest_missingUsername() throws Exception {
            UpdateUserDTO updateUserDTO = new UpdateUserDTO();
            updateUserDTO.setUsername(null);
            updateUserDTO.setEmail("someNewEmail@gmail.com");
            updateUserDTO.setPassword("someNewPassword");

            mockMvc.perform(put("/api/v1/users/" + testUserId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.username").value("Username is required"))
                    .andDo(print());
        }

        @DisplayName("Should fail when email is missing")
        @Test
        void shouldReturnBadRequest_missingEmail() throws Exception {
            UpdateUserDTO updateUserDTO = new UpdateUserDTO();
            updateUserDTO.setUsername("someNewUsername");
            updateUserDTO.setEmail(null);
            updateUserDTO.setPassword("someNewPassword");

            mockMvc.perform(put("/api/v1/users/" + testUserId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").value("Email is required"))
                    .andDo(print());
        }

        @DisplayName("Should fail when password is missing")
        @Test
        void shouldReturnBadRequest_missingPassword() throws Exception {
            UpdateUserDTO updateUserDTO = new UpdateUserDTO();
            updateUserDTO.setUsername("someNewUsername");
            updateUserDTO.setEmail("someNewEmail@gmail.com");
            updateUserDTO.setPassword(null);

            mockMvc.perform(put("/api/v1/users/" + testUserId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.password").value("Password is required"))
                    .andDo(print());




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