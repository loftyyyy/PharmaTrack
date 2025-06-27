package com.rho.ims.service;

import com.rho.ims.dto.RoleDTO;
import com.rho.ims.dto.UpdateRoleDTO;
import com.rho.ims.model.Role;
import com.rho.ims.respository.RoleRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    public Role createRole(RoleDTO role){
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role name is already taken!");
        }

        Role newRole = new Role();
        newRole.setName(role.getName());
        return roleRepository.save(newRole);
    }

    public void deleteRole(long id){
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        roleRepository.delete(existingRole);

    }

    public Role updateRole(Long id, UpdateRoleDTO updateRoleDTO){
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        if(roleRepository.existsByName(updateRoleDTO.getName()) || role.getName().equals(updateRoleDTO.getName())){
            throw new RuntimeException("Name Already Exists!");

        }
        System.out.println(updateRoleDTO.getName());
        role.setName(updateRoleDTO.getName());
        return roleRepository.save(role);

    }

}
