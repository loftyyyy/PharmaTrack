package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.LoginDTO;
import com.rho.ims.dto.SignupDTO;
import com.rho.ims.dto.UserUpdateDTO;
import com.rho.ims.dto.UserResponseDTO;
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

    public User createUser(SignupDTO signupDTO) {
        if (userRepository.existsByUsername(signupDTO.getUsername())) {
            throw new DuplicateCredentialException("username", signupDTO.getUsername());
        }
        if (userRepository.existsByEmail(signupDTO.getEmail())) {
            throw new DuplicateCredentialException("email", signupDTO.getEmail());
        }


        Role role = roleRepository.findById(signupDTO.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
        User user = new User();
        user.setUsername(signupDTO.getUsername());
        user.setPassword(signupDTO.getPassword());
        user.setEmail(signupDTO.getEmail());
        user.setRole(role);
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public User loginUser(LoginDTO loginDTO) {
        User existingUser = userRepository.findByUsernameOrEmail(loginDTO.getUsername(), loginDTO.getUsername());
        if (existingUser == null || !passwordEncoder.matches(loginDTO.getPassword(), existingUser.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return existingUser;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user", id.toString()));
    }

    public List<UserResponseDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserResponseDTO(user)).toList();

    }

    public User updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user", id.toString()));

        Map<String,String> duplicates = new HashMap<>();

        if (userUpdateDTO.getUsername() != null) {
            if(userRepository.existsByUsername(userUpdateDTO.getUsername())){
                duplicates.put("username", userUpdateDTO.getUsername());
            }
        }
        if (userUpdateDTO.getEmail() != null) {
            if(userRepository.existsByEmail(userUpdateDTO.getEmail())){
                duplicates.put("email", userUpdateDTO.getEmail());

            }
        }
        if(!duplicates.isEmpty()){
            throw new DuplicateCredentialException(duplicates);

        }

        if (userUpdateDTO.getPassword() != null) {
            existingUser.setEmail(userUpdateDTO.getEmail());
            existingUser.setUsername(userUpdateDTO.getUsername());
            existingUser.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        //TODO: Future enhancement, implement the updatedBy field

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(existingUser);
    }

}
