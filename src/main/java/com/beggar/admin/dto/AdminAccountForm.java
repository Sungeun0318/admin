package com.beggar.admin.dto;

import com.beggar.admin.entity.AdminAccount;

public class AdminAccountForm {

    private String username;
    private String password;
    private String displayName;
    private AdminAccount.Role role = AdminAccount.Role.VIEWER;
    private AdminAccount.Status status = AdminAccount.Status.ACTIVE;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public AdminAccount.Role getRole() {
        return role;
    }

    public void setRole(AdminAccount.Role role) {
        this.role = role;
    }

    public AdminAccount.Status getStatus() {
        return status;
    }

    public void setStatus(AdminAccount.Status status) {
        this.status = status;
    }
}
