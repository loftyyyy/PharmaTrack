package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.role.RoleCreateDTO;
import com.rho.ims.dto.role.RoleUpdateDTO;
import com.rho.ims.model.Role;
import com.rho.ims.respository.RoleRepository;
import com.rho.ims.respository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public Role findById(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    public Role saveRole(RoleCreateDTO role){

        if (roleRepository.existsByName(role.getName())) {
            throw new DuplicateCredentialException("Role", role.getName());
        }

        Role newRole = new Role();
        newRole.setName(role.getName());
        return roleRepository.save(newRole);
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public Long countUsersInRole(Long id) {
        roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        Long count = userRepository.countByRoleId(id);
        return count;
    }

    public Role updateRole(Long id, RoleUpdateDTO roleUpdateDTO){
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        if(roleRepository.existsByName(roleUpdateDTO.getName()) || role.getName().equals(roleUpdateDTO.getName())){
            throw new RuntimeException("Name Already Exists!");
        }

        role.setName(roleUpdateDTO.getName());
        return roleRepository.save(role);

    }

}
