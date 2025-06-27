package com.rho.ims.dto;

import lombok.Getter;

@Getter
public class SignupDTO {
    private String username;
    private String password;
    private String email;
    private Long roleId;

    public SignupDTO() {
    }
    public SignupDTO(String username, String password, String email, Long roleId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roleId = roleId;
    }


}
