package com.compass.e_commerce.model.role;

public enum RoleName {
    ADMIN("admin"),
    USER("user");

    private String role;

    RoleName(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
