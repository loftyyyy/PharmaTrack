package com.rho.ims.controller;

import com.rho.ims.dto.LoginDTO;
import com.rho.ims.dto.SignupDTO;
import com.rho.ims.dto.UserUpdateDTO;
import com.rho.ims.dto.UserResponseDTO;
import com.rho.ims.model.User;
import com.rho.ims.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@Valid @RequestBody SignupDTO signupDTO) {
            userService.saveUser(signupDTO);
            return ResponseEntity.ok("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.loginUser(loginDTO);
        return ResponseEntity.ok("Login Successfully");

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
            //TODO: Future enhancement, implement the updatedBy field

            User updatedUser = userService.updateUser(id, userUpdateDTO);

//            return ResponseEntity.ok(new UserResponseDTO(updatedUser));

            return ResponseEntity.ok().body("Successfully updated profile");

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");

    }

}


