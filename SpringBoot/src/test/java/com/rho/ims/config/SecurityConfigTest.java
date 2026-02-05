package com.rho.ims.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPasswordEncoderBean() {
        assertNotNull(passwordEncoder);
        
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void testPasswordEncoderDifferentPasswords() {
        String password1 = "password1";
        String password2 = "password2";
        
        String encoded1 = passwordEncoder.encode(password1);
        String encoded2 = passwordEncoder.encode(password2);
        
        // Different passwords should produce different encodings
        assertNotEquals(encoded1, encoded2);
        
        // Each password should match its own encoding
        assertTrue(passwordEncoder.matches(password1, encoded1));
        assertTrue(passwordEncoder.matches(password2, encoded2));
        
        // But not match the other's encoding
        assertFalse(passwordEncoder.matches(password1, encoded2));
        assertFalse(passwordEncoder.matches(password2, encoded1));
    }

    @Test
    void testPasswordEncoderSamePasswordDifferentEncodings() {
        String password = "samePassword";
        
        String encoded1 = passwordEncoder.encode(password);
        String encoded2 = passwordEncoder.encode(password);
        
        // Same password should produce different encodings (due to salt)
        assertNotEquals(encoded1, encoded2);
        
        // But both should match the original password
        assertTrue(passwordEncoder.matches(password, encoded1));
        assertTrue(passwordEncoder.matches(password, encoded2));
    }
}
