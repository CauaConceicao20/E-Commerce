package com.compass.e_commerce.model.enums;

public enum RoleNameEnum {
    ADMIN("admin"),
    USER("user");

    private String role;

    RoleNameEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
