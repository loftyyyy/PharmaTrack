package com.rho.ims.controller;

import com.rho.ims.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class PurchaseControllerIntegrationTest {

    @Nested
    class CreatePurchaseTest {
        @BeforeEach
        void setup(){

        }


    }

    @Nested
    class FetchPurchaseTest {
        @BeforeEach
        void setup(){

        }

    }

    @Nested
    class UpdatePurchaseTest {
        @BeforeEach
        void setup(){

        }

    }

    @Nested
    class DeletePurchaseTest {
        @BeforeEach
        void setup(){

        }

    }


}