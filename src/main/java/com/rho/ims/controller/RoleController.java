package com.rho.ims.controller;

import com.rho.ims.dto.RoleCreateDTO;
import com.rho.ims.dto.RoleResponseDTO;
import com.rho.ims.dto.RoleUpdateDTO;
import com.rho.ims.model.Role;
import com.rho.ims.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleCreateDTO roleCreateDTO) {
        Role role = roleService.createRole(roleCreateDTO);

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
    public ResponseEntity<?> updateRole(@PathVariable long id, @Valid @RequestBody RoleUpdateDTO roleUpdateDTO){
        roleService.updateRole(id, roleUpdateDTO);
        return ResponseEntity.ok().body("Role Updated Successfully");




    }





    
    
}
