package com.example.account_service.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIn {

    @NonNull
    private String employee;

    @NonNull
    @Pattern(regexp = "(0[1-9]|1[0-2])-\\d{4}")
    private String period;

    @NonNull
    @Min(value = 0)
    private Long salary;
}
