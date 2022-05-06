package com.example.account_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccess {
    @NotBlank
    private String user;
    @NotBlank
    @Pattern(regexp = "^(LOCK|UNLOCK)$")
    private String operation;
}
