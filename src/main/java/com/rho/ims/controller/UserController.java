package com.rho.ims.controller;

import com.rho.ims.dto.LoginDTO;
import com.rho.ims.dto.SignupDTO;
import com.rho.ims.dto.UpdateUserDTO;
import com.rho.ims.dto.UserResponseDTO;
import com.rho.ims.model.Role;
import com.rho.ims.model.User;
import com.rho.ims.service.RoleService;
import com.rho.ims.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;

    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody SignupDTO signupDTO) {
        try {
            Role role = roleService.findById(signupDTO.getRoleId());
            User user = new User();
            user.setUsername(signupDTO.getUsername());
            user.setPassword(signupDTO.getPassword());
            user.setEmail(signupDTO.getEmail());
            user.setRole(role);
            userService.createUser(user);
            return ResponseEntity.ok("User created successfully");
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User creation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
       }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        User logUser = userService.loginUser(loginDTO);
        if (logUser == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(error);
        }
        return ResponseEntity.ok(new LoginDTO(logUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id) {
        User user = userService.findById(id);
        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User not found");
            return ResponseEntity.status(404).body(error);
        }
        return ResponseEntity.ok(new UserResponseDTO(user));

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody UpdateUserDTO updateUserDTO) {
        try {
            //TODO: Future enhancement, implement the updatedBy field

            User updatedUser = userService.updateUser(id, updateUserDTO);

            return ResponseEntity.ok(new UserResponseDTO(updatedUser));

        }catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User update failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        try{
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        }catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User deletion failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }

    }

}


