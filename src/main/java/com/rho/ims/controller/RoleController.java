package com.rho.ims.controller;

import com.rho.ims.dto.RoleDTO;
import com.rho.ims.dto.RoleResponseDTO;
import com.rho.ims.dto.UpdateRoleDTO;
import com.rho.ims.model.Role;
import com.rho.ims.service.RoleService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        Role role = roleService.createRole(roleDTO);

        return ResponseEntity.ok("Role created successfully: " + role.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok("Role deleted successfully");

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRole(@PathVariable long id) {
        Role role = roleService.findById(id);
        return ResponseEntity.ok(new RoleResponseDTO(role));


    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable long id, @Valid @RequestBody UpdateRoleDTO updateRoleDTO){
        roleService.updateRole(id, updateRoleDTO);
        return ResponseEntity.ok().body("Role Updated Successfully");




    }





    
    
}
