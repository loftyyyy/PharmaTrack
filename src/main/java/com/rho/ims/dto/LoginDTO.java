package com.rho.ims.dto;

import com.rho.ims.model.User;
import lombok.Getter;

@Getter
public class LoginDTO {
    private String username;
    private String password;

    public LoginDTO() {
    }
    public LoginDTO(User user){
        this.username = user.getUsername();
        this.password = user.getPassword();

    }



}
