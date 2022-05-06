package com.example.account_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePut {
    @NotBlank
    private String user;
    @NotBlank
    private String role;
    @NotBlank
    private String operation;
}
