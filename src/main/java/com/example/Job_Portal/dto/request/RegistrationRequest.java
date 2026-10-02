package com.example.Job_Portal.dto.request;

import com.example.Job_Portal.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class RegistrationRequest {
    @NotBlank
    private String fullname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8,max = 20)
    private String password;

    @NotNull
    private Role role;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String fullname, String email, String password, Role role) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
