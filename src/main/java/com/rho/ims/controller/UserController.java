package com.rho.ims.controller;

import com.rho.ims.dto.LoginDTO;
import com.rho.ims.dto.SignupDTO;
import com.rho.ims.dto.UpdateUserDTO;
import com.rho.ims.dto.UserResponseDTO;
import com.rho.ims.model.Role;
import com.rho.ims.model.User;
import com.rho.ims.service.RoleService;
import com.rho.ims.service.UserService;
import jakarta.validation.Valid;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@Valid @RequestBody SignupDTO signupDTO) {
            userService.createUser(signupDTO);
            return ResponseEntity.ok("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDTO loginDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            Map<String, List<String>> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                String field = error.getField();
                String message = error.getDefaultMessage();
                errors.computeIfAbsent(field, key -> new ArrayList<>()).add(message);
            }

            return ResponseEntity.badRequest().body(errors);
        }

        try{
            User user = userService.loginUser(loginDTO);
            return ResponseEntity.ok("Login Successfully");

        }catch (RuntimeException e){
            Map<String, String> error = new HashMap<>();
            error.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);

        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id) {
        try{
            User user = userService.findById(id);
            return ResponseEntity.ok(new UserResponseDTO(user));

        }catch (RuntimeException e){
            Map<String, String> error = new HashMap<>();
            error.put("message", "User retrieval failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllUser(){
        try{
            List<UserResponseDTO> users = userService.getAllUsers();

            return ResponseEntity.ok().body(users);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @Valid @RequestBody UpdateUserDTO updateUserDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);

        }
        try {
            //TODO: Future enhancement, implement the updatedBy field

            User updatedUser = userService.updateUser(id, updateUserDTO);

//            return ResponseEntity.ok(new UserResponseDTO(updatedUser));

            return ResponseEntity.ok().body("Successfully updated profile");
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


