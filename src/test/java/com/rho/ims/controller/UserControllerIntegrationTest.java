package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.SignupDTO;
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

    @Test
    void loginUser() {
    }

    @Test
    void getUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }
}