package com.example.account_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER("USER"),
    ROLE_ACCOUNTANT("ACCOUNTANT"),
    ROLE_ADMINISTRATOR("ADMINISTRATOR"),
    ROLE_AUDITOR("AUDITOR");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Role fromValue(String value) {
        for (Role someEnum : Role.values()) {
            if (someEnum.getValue().equals(value) || someEnum.getValue().equals("ROLE_" + value)) {
                return someEnum;
            }
        }
        throw new IllegalArgumentException("Unknown value " + value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
