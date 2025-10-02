package com.rho.ims.controller;

import com.rho.ims.dto.RoleCreateDTO;
import com.rho.ims.dto.RoleResponseDTO;
import com.rho.ims.dto.RoleUpdateDTO;
import com.rho.ims.model.Role;
import com.rho.ims.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleCreateDTO roleCreateDTO) {
        Role role = roleService.saveRole(roleCreateDTO);
        return ResponseEntity.ok("Role created successfully: " + role.getName());
    }

    @GetMapping()
    public ResponseEntity<?> getAllRole(){
        List<RoleResponseDTO> roles = roleService.getAll().stream().map(role -> new RoleResponseDTO(role)).toList();
        return ResponseEntity.ok().body(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRole(@PathVariable long id) {
        Role role = roleService.findById(id);
        return ResponseEntity.ok(new RoleResponseDTO(role));
    }

    @GetMapping("/{id}/users/count")
    public ResponseEntity<Long> getUserCountByRoleId(@PathVariable long id) {
        long count = roleService.countUsersInRole(id);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable long id, @Valid @RequestBody RoleUpdateDTO roleUpdateDTO){
        roleService.updateRole(id, roleUpdateDTO);
        return ResponseEntity.ok().body("Role Updated Successfully");
    }





    
    
}
