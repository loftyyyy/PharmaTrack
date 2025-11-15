package com.rho.ims.controller;

import com.rho.ims.dto.auth.RegisterRequest;
import com.rho.ims.dto.user.UserResponseDTO;
import com.rho.ims.dto.user.UserUpdateDTO;
import com.rho.ims.model.User;
import com.rho.ims.service.OtpService;
import com.rho.ims.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final OtpService otpService;

    public UserController(UserService userService, OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequest registerRequest) {
            userService.saveUser(registerRequest);
            return ResponseEntity.ok("User created successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email){

        if(!userService.existsByEmail(email)){
            return ResponseEntity.badRequest().body("Email not found");
        }

        String otp = otpService.generateOtp(email);


        return ResponseEntity.ok().body("OTP sent successfully!");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        User user = userService.findByName(authentication.getName());
        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    @GetMapping()
    public ResponseEntity<?> getAllUser(){
        List<UserResponseDTO> users = userService.getAll();
        return ResponseEntity.ok().body(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        User updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok().body("Successfully updated profile");
    }

}


