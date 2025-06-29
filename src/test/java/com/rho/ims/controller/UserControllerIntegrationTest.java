package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.LoginDTO;
import com.rho.ims.dto.SignupDTO;
import com.rho.ims.model.Role;
import com.rho.ims.model.User;
import com.rho.ims.service.RoleService;
import com.rho.ims.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerIntegrationTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    RoleService roleService;

    @Nested
    class CreateUserTest {

        @Test
        void shouldCreateUserSuccessfully(){
            try{

                SignupDTO signupDTO = new SignupDTO();
                signupDTO.setUsername("PokemonHunter");
                signupDTO.setEmail("integrationTest@gmail.com");
                signupDTO.setPassword("Pokemon321");
                signupDTO.setRoleId(1L);

                mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(content().string("User created successfully"))
                        .andDo(print());

            }catch (Exception e){
                e.printStackTrace();
            }


        }

        @Test
        void shouldReturnBadRequest_whenUsernameIsBlank(){
            try{
                SignupDTO signupDTO = new SignupDTO();
                signupDTO.setUsername("");
                signupDTO.setEmail("integrationTest@gmail.com");
                signupDTO.setPassword("Pokemon321");
                signupDTO.setRoleId(1L);

                mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signupDTO)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.username")
                                .value("Username is required")).andDo(print());

            }catch (Exception e){
                e.printStackTrace();

            }

        }

        @Test
        void shouldReturnBadRequest_whenEmailIsBlank(){
            SignupDTO signupDTO = new SignupDTO();
            signupDTO.setUsername("PokemonHunter");
            signupDTO.setEmail("");
            signupDTO.setPassword("Pokemon321");
            signupDTO.setRoleId(1L);
            try{
                mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.email").value("email is required")).andDo(print());

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Test
        void shouldReturnBadRequest_whenPasswordIsBlank(){

            try{

                SignupDTO signupDTO = new SignupDTO();
                signupDTO.setUsername("PokemonHunter");
                signupDTO.setEmail("integrationTest@gmail.com");
                signupDTO.setPassword("");
                signupDTO.setRoleId(1L);

                mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.password").value("Password is required"))
                        .andDo(print());

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Test
        void shouldReturnBadRequest_whenRoleIsNull(){

            try{
                SignupDTO signupDTO = new SignupDTO();
                signupDTO.setUsername("PokemonHunter");
                signupDTO.setEmail("integrationTest@gmail.com");
                signupDTO.setPassword("Pokemon321");
                signupDTO.setRoleId(null);

                mockMvc.perform(post("/api/v1/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupDTO)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.roleId").value("Role id is required"))
                        .andDo(print());

            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }

    @Nested
    class LogInUserTest{

        @Test
        void shouldReturnSuccessfulLogin(){
            try{

                LoginDTO loginDTO = new LoginDTO();
                loginDTO.setUsername("rho");
                loginDTO.setPassword("cocgamer");

                mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(content().string("Login Successfully"))
                        .andDo(print());

            }catch(Exception e){
                e.printStackTrace();
            }

        }

        @Test
        void shouldReturnBadRequest_wrongUsername(){

            try{

                LoginDTO loginDTO = new LoginDTO();
                loginDTO.setUsername("pokemon");
                loginDTO.setPassword("cocgamer");

                mockMvc.perform(post("api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.username").value("Invalid username or password"))
                        .andDo(print());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Test
        void shouldReturnBadRequest_wrongPassword(){

            try{

                LoginDTO loginDTO = new LoginDTO();
                loginDTO.setUsername("rho");
                loginDTO.setPassword("cocgamer123");

                mockMvc.perform(post("api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.password").value("Invalid username or password"))
                        .andDo(print());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Test
        void shouldReturnBadRequest_usernameIsBlank(){
            try{
                LoginDTO loginDTO = new LoginDTO();
                loginDTO.setUsername("");
                loginDTO.setPassword("cocgamer");

                mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.username").value("Username is required"))
                        .andDo(print());

            }catch (Exception e){
                e.printStackTrace();

            }


        }

        @Test
        void shouldReturnBadRequest_passwordIsBlank(){
           try{

               LoginDTO loginDTO = new LoginDTO();
               loginDTO.setUsername("rho");
               loginDTO.setPassword("");

               mockMvc.perform(post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                       .andExpect(status().isBadRequest())
                       .andExpect(jsonPath("$.password").value("Password is required"))
                       .andDo(print());


           }catch (Exception e){
               e.printStackTrace();

           }

        }



    }

    @Nested
    class getUserTest {

        @Test
        void shouldReturnSuccessfulRetrieval(){
            try{
                User user = new User();
                user.setUsername("rho");
                user.setEmail("lofty8.business@gmail.com");
                user.setRole(new Role());
                user.setId(6L);

                when(userService.findById(6L)).thenReturn(user);


                mockMvc.perform(get("/api/v1/users/6").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(jsonPath("$.username").value("rho"))
                        .andExpect(jsonPath("$.email").value("lofty8.business@gmail.com"))
                        .andDo(print());


            }catch (Exception e){
                e.printStackTrace();
            }

        }
        @Test
        void shouldReturnBadRequestRetrieval(){
            try{
                when(userService.findById(9L)).thenThrow(new RuntimeException("User not found"));


                mockMvc.perform(get("/api/v1/users/9").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("User retrieval failed: User not found"))
                        .andDo(print());


            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }
}