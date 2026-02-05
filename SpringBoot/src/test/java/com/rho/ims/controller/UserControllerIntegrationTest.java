//package com.rho.ims.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rho.ims.config.SecurityConfig;
//import com.rho.ims.dto.user.UserUpdateDTO;
//import com.rho.ims.model.Role;
//import com.rho.ims.model.User;
//import com.rho.ims.respository.RoleRepository;
//import com.rho.ims.respository.UserRepository;
//import com.rho.ims.service.RoleService;
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
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//
//
//import javax.sql.DataSource;
//
//import java.util.List;
//
//import static org.hamcrest.Matchers.anyOf;
//import static org.hamcrest.Matchers.is;
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Import(SecurityConfig.class)
//@TestPropertySource(locations = "classpath:application-test.properties")
//@ActiveProfiles("test")
//class UserControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private RoleService roleService;
//
//    @Value("${test.user.username}")
//    private String username;
//
//    @Value("${test.user.email}")
//    private String email;
//
//    @Value("${test.user.password}")
//    private String password;
//
//
//    @Nested
//    class CreateUserTest {
//        User user;
//        Role role;
//
//
//        @BeforeEach
//        void setup(){
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            role = new Role();
//            role.setName("Test Role");
//
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setEmail(email);
//            user.setPassword(password);
//            user.setRole(role);
//            userRepository.save(user);
//
//
//        }
//
//        @DisplayName("Should create a new user")
//        @Test
//        void shouldReturnSuccessfulRequest_createUser() throws Exception{
//            String roleName = role.getName();
//
//            SignupDTO signupDTO = new SignupDTO();
//            signupDTO.setUsername("SomeUserName");
//            signupDTO.setEmail("SomeEmail@Gmail.com");
//            signupDTO.setPassword("somePassword");
//            signupDTO.setRoleName(roleName);
//
//            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
//                    .andExpect(status().is2xxSuccessful())
//                    .andExpect(content().string("User created successfully"))
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when username already exist")
//        @Test
//        void shouldReturnBadRequest_usernameIsTaken()throws Exception {
//            String roleName = role.getName();
//
//            SignupDTO signupDTO = new SignupDTO();
//            signupDTO.setUsername("SomeUserName");
//            signupDTO.setEmail("SomeEmail@Gmail.com");
//            signupDTO.setPassword("somePassword");
//            signupDTO.setRoleName(roleName);
//
//            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
//                    .andExpect(status().is4xxClientError())
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail when username is blank")
//        @Test
//        void shouldReturnBadRequest_usernameIsBlank() throws Exception{
//            String roleName = role.getName();
//
//            SignupDTO signupDTO = new SignupDTO();
//            signupDTO.setUsername("SomeUserName");
//            signupDTO.setEmail("SomeEmail@Gmail.com");
//            signupDTO.setPassword("somePassword");
//            signupDTO.setRoleName(roleName);
//
//            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors['username']").value("Username is required"))
//                    .andDo(print());
//
//
//        }
//
//        @DisplayName("Should fail when email is blank")
//        @Test
//        void shouldReturnBadRequest_emailIsBlank() throws Exception {
//            String roleName = role.getName();
//
//            SignupDTO signupDTO = new SignupDTO();
//            signupDTO.setUsername("SomeUserName");
//            signupDTO.setEmail("SomeEmail@Gmail.com");
//            signupDTO.setPassword("somePassword");
//            signupDTO.setRoleName(roleName);
//
//            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors['email']").value("Email is required"))
//                    .andDo(print());
//
//
//        }
//
//        @DisplayName("Should fail when email is invalid")
//        @Test
//        void shouldReturnBadRequest_emailIsInvalid() throws Exception {
//            Long roleId = role.getId();
//
//            SignupDTO signupDTO = new SignupDTO();
//            signupDTO.setUsername(username);
//            signupDTO.setEmail("someInvalidEmail@.com");
//            signupDTO.setPassword(password);
//
//            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors['email']").value("Invalid email"))
//                    .andDo(print());
//
//
//        }
//
//        @DisplayName("Should fail when password is blank")
//        @Test
//        void shouldReturnBadRequest_passwordIsBlank() throws Exception{
//            Long roleId = role.getId();
//
//            SignupDTO signupDTO = new SignupDTO();
//            signupDTO.setUsername(username);
//            signupDTO.setEmail("someInvalidEmail@.com");
//            signupDTO.setPassword(password);
//
//            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors['password']").value(anyOf(is("Password is required"), is("Password must be at least 8 characters"))))
//                    .andDo(print());
//
//
//        }
//
//        @DisplayName("Should fail when role is null")
//        @Test
//        void shouldReturnBadRequest_roleIsNull() throws Exception {
//
//            SignupDTO signupDTO = new SignupDTO();
//            signupDTO.setUsername(username);
//            signupDTO.setEmail(email);
//            signupDTO.setPassword(password);
//            signupDTO.setRoleName(null);
//
//            mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors['roleId']").value("Role id is required"))
//                    .andDo(print());
//
//
//
//        }
//
//
//
//    }
//
//    @Nested
//    class LogInUserTest {
//        User user;
//        Role role;
//
//        @BeforeEach
//        void setup(){
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            role = new Role();
//            role.setName("Test Role");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setPassword(passwordEncoder.encode(password));
//            user.setEmail(email);
//            user.setRole(role);
//            userRepository.save(user);
//        }
//
//        @DisplayName("Should Log in the user")
//        @Test
//        void shouldReturnSuccessfulRequest_LogIn() throws Exception {
//            LoginDTO loginDTO = new LoginDTO();
//            loginDTO.setUsername(username);
//            loginDTO.setPassword(password);
//
//            mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
//                    .andExpect(status().is2xxSuccessful())
//                    .andExpect(content().string("Login Successfully"))
//                    .andDo(print());
//
//        }
//
//        @DisplayName("Should fail with wrong username")
//        @Test
//        void shouldReturnServerErrorRequest_wrongUsername() throws Exception {
//            LoginDTO loginDTO = new LoginDTO();
//            loginDTO.setUsername("SomeWrongUsername");
//            loginDTO.setPassword(password);
//
//            mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
//                    .andExpect(status().isInternalServerError())
//                    .andExpect(jsonPath("$.message").value("Invalid username or password"))
//                    .andDo(print());
//        }
//
//        @DisplayName("Should fail with wrong password")
//        @Test
//        void shouldReturnInternalServerErrorRequest_wrongPassword() throws Exception {
//
//
//            LoginDTO loginDTO = new LoginDTO();
//            loginDTO.setUsername(username);
//            loginDTO.setPassword("someWrongPassword");
//
//            mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
//                    .andExpect(status().isInternalServerError())
//                    .andExpect(jsonPath("$.message").value("Invalid username or password"))
//                    .andDo(print());
//
//
//
//        }
//
//        @DisplayName("Should fail with blank username")
//        @Test
//        void shouldReturnBadRequest_blankUsername() throws Exception{
//
//            LoginDTO loginDTO = new LoginDTO();
//            loginDTO.setUsername("");
//            loginDTO.setPassword(password);
//
//            mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors['username']").value("Username is required"))
//                    .andDo(print());
//
//
//
//
//
//        }
//
//        @DisplayName("Should fail with blank password")
//        @Test
//        void shouldReturnBadRequest_blankPassword() throws Exception{
//
//            LoginDTO loginDTO = new LoginDTO();
//            loginDTO.setUsername(username);
//            loginDTO.setPassword("");
//
//            mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors.password").value(anyOf(is("Password is required"),is("Password must be at least 8 characters"))))
//                    .andDo(print());
//
//
//
//
//
//        }
//
//    }
//
//    @Nested
//    class GetUserTest {
//        User user;
//        Role role;
//
//        @BeforeEach
//        void setup(){
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            role = new Role();
//            role.setName("Test Role");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setPassword(password);
//            user.setEmail(email);
//            user.setRole(role);
//            userRepository.save(user);
//
//
//
//        }
//
//        @DisplayName("Should return a user")
//        @Test
//        void shouldReturnSuccessfulRequest_getUser() throws Exception{
//            Long roleId = role.getId();
//            Long userId = user.getId();
//
//            String roleName = roleService.findById(roleId).getName();
//
//            mockMvc.perform(get("/api/v1/users/" + userId).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().is2xxSuccessful())
//                    .andExpect(jsonPath("$.username").value(username))
//                    .andExpect(jsonPath("$.email").value(email))
//                    .andExpect(jsonPath("$.roleName").value(roleName))
//                    .andDo(print());
//        }
//
//        @DisplayName("Should fail with invalid id format")
//        @Test
//        void shouldReturnBadRequest_invalidId() throws Exception{
//            // TODO: Might implement a Controller Advice
//            String id = "xyz";
//
//            mockMvc.perform(get("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isInternalServerError())
//                    .andDo(print());
//
//
//
//        }
//
//        @DisplayName("Should fail with id that doesn't exist")
//        @Test
//        void shouldReturnNotFoundRequest_idNotExist() throws Exception {
//            long id = 999;
//
//            mockMvc.perform(get("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.message").value("user not found"))
//                    .andDo(print());
//        }
//
//
//
//    }
//
//    @Nested
//    class GetAllUsersTest {
//        Role pharmacist;
//        Role cashier;
//        User user1;
//        User user2;
//
//        @BeforeEach
//        void setup(){
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            Role pharmacist = new Role();
//            pharmacist.setName("Pharmacist");
//
//            Role cashier = new Role();
//            cashier.setName("Cashier");
//
//            roleRepository.saveAll(List.of(pharmacist,cashier));
//
//            this.pharmacist = pharmacist;
//            this.cashier = cashier;
//
//
//
//            User user1 = new User();
//            user1.setUsername("User1");
//            user1.setEmail("pokemon@gmail.com");
//            user1.setPassword("password123");
//            user1.setRole(pharmacist);
//
//            User user2 = new User();
//            user2.setUsername("User2");
//            user2.setEmail("pokemon2@gmail.com");
//            user2.setPassword("password123");
//            user2.setRole(cashier);
//            userRepository.saveAll(List.of(user1, user2));
//
//            this.user1 = user1;
//            this.user2 = user2;
//
//        }
//
//        @DisplayName("should get all users")
//        @Test
//        void shouldReturnSuccessfulRequest_returnAllUsers() throws Exception{
//
//            mockMvc.perform(get("/api/v1/users").contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.length()").value(2))
//                    .andExpect(jsonPath("$[0].username").value(user1.getUsername()))
//                    .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
//                    .andExpect(jsonPath("$[0].roleName").value(user1.getRole().getName()))
//
//                    .andExpect(jsonPath("$[1].username").value(user2.getUsername()))
//                    .andExpect(jsonPath("$[1].email").value(user2.getEmail()))
//                    .andExpect(jsonPath("$[1].roleName").value(user2.getRole().getName()))
//                    .andDo(print());
//
//        }
//
//
//    }
//
//    @Nested
//    class UpdateUserTest {
//        User user;
//        Role role;
//
//
//        @BeforeEach
//        void setup(){
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            role = new Role();
//            role.setName("Test Role");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setPassword(password);
//            user.setEmail(email);
//            user.setRole(role);
//
//            userRepository.save(user);
//        }
//
//
//        @DisplayName("Should update the user")
//        @Test
//        void shouldReturnSuccessfulRequest_updateUser() throws Exception {
//            Long id = user.getId();
//
//            UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
//            userUpdateDTO.setUsername("someNewUsername");
//            userUpdateDTO.setEmail("someNewEmail@gmail.com");
//            userUpdateDTO.setPassword("someNewPassword");
//
//            mockMvc.perform(put("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userUpdateDTO)))
//                    .andExpect(status().is2xxSuccessful())
//                    .andExpect(content().string("Successfully updated profile"))
//                    .andDo(print());
//
//
//        }
//
//
//        @DisplayName("Should fail when username is missing")
//        @Test
//        void shouldReturnBadRequest_missingUsername() throws Exception {
//            Long id = user.getId();
//
//            UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
//            userUpdateDTO.setUsername(null);
//            userUpdateDTO.setEmail("someNewEmail@gmail.com");
//            userUpdateDTO.setPassword("someNewPassword");
//
//            mockMvc.perform(put("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userUpdateDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors['username']").value("Username is required"))
//                    .andDo(print());
//        }
//
//        @DisplayName("Should fail when email is missing")
//        @Test
//        void shouldReturnBadRequest_missingEmail() throws Exception {
//            Long id = user.getId();
//
//            UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
//            userUpdateDTO.setUsername("someNewUsername");
//            userUpdateDTO.setEmail(null);
//            userUpdateDTO.setPassword("someNewPassword");
//
//            mockMvc.perform(put("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userUpdateDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors['email']").value("Email is required"))
//                    .andDo(print());
//        }
//
//        @DisplayName("Should fail when password is missing")
//        @Test
//        void shouldReturnBadRequest_missingPassword() throws Exception {
//            Long id = user.getId();
//
//            UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
//            userUpdateDTO.setUsername("someNewUsername");
//            userUpdateDTO.setEmail("someNewEmail@gmail.com");
//            userUpdateDTO.setPassword(null);
//
//            mockMvc.perform(put("/api/v1/users/" + id ).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userUpdateDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors['password']").value("Password is required"))
//                    .andDo(print());
//
//
//        }
//
//        @DisplayName("Should fail when username already exist")
//        @Test
//        void shouldConflictRequest_usernameAlreadyExist() throws Exception {
//            Long id = user.getId();
//
//            UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
//            userUpdateDTO.setUsername(username);
//            userUpdateDTO.setEmail("SomeEmail@gmail.com");
//            userUpdateDTO.setPassword("newPassword");
//
//            mockMvc.perform(put("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userUpdateDTO)))
//                    .andExpect(status().isConflict())
//                    .andExpect(jsonPath("$.message").value("Duplicate credential(s): username already exist"))
//                    .andDo(print());
//
//
//
//
//        }
//
//        @DisplayName("Should fail when email already exist")
//        @Test
//        void shouldReturnConflictRequest_emailAlreadyExist() throws Exception {
//            Long id = user.getId();
//
//            UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
//            userUpdateDTO.setUsername("SomeUserName");
//            userUpdateDTO.setEmail(email);
//            userUpdateDTO.setPassword("newPassword");
//
//            mockMvc.perform(put("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userUpdateDTO)))
//                    .andExpect(status().isConflict())
//                    .andExpect(jsonPath("$.message").value("Duplicate credential(s): email already exist"))
//                    .andDo(print());
//
//
//
//
//        }
//
//        @DisplayName("Should fail when username and email already exist")
//        @Test
//        void shouldReturnConflictRequest_usernameAndEmailAlreadyExist() throws Exception {
//            Long id = user.getId();
//
//            UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
//            userUpdateDTO.setUsername(username);
//            userUpdateDTO.setEmail(email);
//            userUpdateDTO.setPassword("newPassword");
//
//            mockMvc.perform(put("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userUpdateDTO)))
//                    .andExpect(status().isConflict())
//                    .andExpect(jsonPath("$.message").value(anyOf(is("Duplicate credential(s): username, email already exist"),is("Duplicate credential(s): email, username already exist"))))
//                    .andDo(print());
//
//        }
//
//
//
//
//    }
//
//    @Nested
//    class DeleteUserTest {
//        User user;
//        Role role;
//
//        @BeforeEach
//        void setup(){
//            userRepository.deleteAll();
//            roleRepository.deleteAll();
//
//            role = new Role();
//            role.setName("Test Role");
//            roleRepository.save(role);
//
//            user = new User();
//            user.setUsername(username);
//            user.setPassword(password);
//            user.setEmail(email);
//            user.setRole(role);
//
//            userRepository.save(user);
//
//        }
//
//        @DisplayName("Should delete user")
//        @Test
//        void shouldReturnSuccessfulRequest_deleteUser() throws Exception {
//            Long id = user.getId();
//
//            mockMvc.perform(delete("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().is2xxSuccessful())
//                    .andExpect(content().string("User deleted successfully"))
//                    .andDo(print());
//
//
//
//        }
//
//        @DisplayName("Should fail when the user is invalid or doesn't exist")
//        @Test
//        void shouldReturnInternalServerErrorRequest_invalidUser() throws Exception {
//            long someInvalidId = 99;
//
//            mockMvc.perform(delete("/api/v1/users/" + someInvalidId).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isInternalServerError())
//                    .andExpect(jsonPath("$.message").value("User not found"))
//                    .andDo(print());
//        }
//
//    }
//
//}