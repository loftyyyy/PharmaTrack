package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.config.SecurityConfig;
import com.rho.ims.dto.LoginDTO;
import com.rho.ims.dto.SignupDTO;
import com.rho.ims.dto.UpdateUserDTO;
import com.rho.ims.model.Role;
import com.rho.ims.model.User;
import com.rho.ims.service.RoleService;
import com.rho.ims.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    UserService userService;

    @MockBean
    RoleService roleService;

    @Nested
    class CreateUserTest {

        @Test
        void shouldReturnSuccessfulRequest_createUser(){

        }

    }

    @Nested
    class LogInUserTest {

    }

    @Nested
    class GetUserTest {

    }

    @Nested
    class UpdateUserTest {

    }

    @Nested
    class DeleteUserTest {

    }

}