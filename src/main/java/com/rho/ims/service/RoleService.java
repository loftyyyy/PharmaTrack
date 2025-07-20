package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.dto.RoleCreateDTO;
import com.rho.ims.dto.RoleUpdateDTO;
import com.rho.ims.model.Role;
import com.rho.ims.respository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findById(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    public Role createRole(RoleCreateDTO role){

        if (roleRepository.existsByName(role.getName())) {
            throw new DuplicateCredentialException("Role", role.getName());
        }

        Role newRole = new Role();
        newRole.setName(role.getName());
        return roleRepository.save(newRole);
    }

    public void deleteRole(Long id){
        Role existingRole = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        roleRepository.delete(existingRole);

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
