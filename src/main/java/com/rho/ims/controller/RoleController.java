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
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleDTO roleDTO, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            Map<String,String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try{
            Role role = roleService.createRole(roleDTO);

            return ResponseEntity.ok("Role created successfully: " + role.getName());

        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error creating role: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable long id) {
        try{
            roleService.deleteRole(id);
            return ResponseEntity.ok("Role deleted successfully");

        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error deleting role: " + e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRole(@PathVariable long id) {
        try{
            Role role = roleService.findById(id);
            return ResponseEntity.ok(new RoleResponseDTO(role));

        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error retrieving role: " + e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable long id, @Valid @RequestBody UpdateRoleDTO updateRoleDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);

        }
        try{
            roleService.updateRole(id, updateRoleDTO);
            return ResponseEntity.ok().body("Role Updated Successfully");

        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error updating role: " + e.getMessage());

        }



    }





    
    
}
