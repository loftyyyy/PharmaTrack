package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.auth.RegisterRequest;
import com.rho.ims.dto.user.UserResponseDTO;
import com.rho.ims.dto.user.UserUpdateDTO;
import com.rho.ims.model.Role;
import com.rho.ims.model.User;
import com.rho.ims.respository.RoleRepository;
import com.rho.ims.respository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User saveUser(RegisterRequest signupDTO) {
        if (userRepository.existsByUsername(signupDTO.getUsername())) {
            throw new DuplicateCredentialException("username", signupDTO.getUsername());
        }
        if (existsByEmail(signupDTO.getEmail())) {
            throw new DuplicateCredentialException("email", signupDTO.getEmail());
        }


        Role role = roleRepository.findByName(signupDTO.getRoleName()).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        User user = new User();
        user.setUsername(signupDTO.getUsername());
        user.setPassword(signupDTO.getPassword());
        user.setEmail(signupDTO.getEmail());
        user.setRole(role);
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }


    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user", id.toString()));
    }

    public User findByName(String name) {
        return userRepository.findByUsername(name);
    }

    public List<UserResponseDTO> getAll(){
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserResponseDTO(user)).toList();

    }

    public User updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user", id.toString()));

        // Require current password
        if (userUpdateDTO.getCurrentPassword() == null ||
                !passwordEncoder.matches(userUpdateDTO.getCurrentPassword(), existingUser.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        Map<String, String> duplicates = new HashMap<>();

        // Check username duplication
        if (userUpdateDTO.getUsername() != null &&
                !userUpdateDTO.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(userUpdateDTO.getUsername())) {
                duplicates.put("username", userUpdateDTO.getUsername());
            }
        }

        // Check email duplication
        if (userUpdateDTO.getEmail() != null &&
                !userUpdateDTO.getEmail().equals(existingUser.getEmail())) {
            if (existsByEmail(userUpdateDTO.getEmail())) {
                duplicates.put("email", userUpdateDTO.getEmail());
            }
        }

        if (!duplicates.isEmpty()) {
            throw new DuplicateCredentialException(duplicates);
        }

        // Update fields only if provided
        if (userUpdateDTO.getUsername() != null) {
            existingUser.setUsername(userUpdateDTO.getUsername());
        }
        if (userUpdateDTO.getEmail() != null) {
            existingUser.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

}
