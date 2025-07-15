//package com.rho.ims.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class TestSecurityConfig {
//    @Bean
//    SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf().disable()
//            .authorizeHttpRequests(auth -> auth
//                // require authentication for *every* API call
//                .anyRequest().authenticated()
//            )
//            .oauth2ResourceServer(oauth2 -> oauth2.jwt());   // or httpBasic/formLogin etc.
//
//        return http.build();
//    }
//
////    @Bean
////    JwtDecoder jwtDecoder() {
////        return token -> Mockito.mock(Jwt.class);   // minimal, never really called
////    }
//}
