package com.beggar.admin.dto;

public class AdminAccountListItem {

    private final Long adminId;
    private final String username;
    private final String displayName;
    private final String roleLabel;
    private final String statusLabel;
    private final String statusClass;
    private final String createdAt;

    public AdminAccountListItem(
            Long adminId,
            String username,
            String displayName,
            String roleLabel,
            String statusLabel,
            String statusClass,
            String createdAt
    ) {
        this.adminId = adminId;
        this.username = username;
        this.displayName = displayName;
        this.roleLabel = roleLabel;
        this.statusLabel = statusLabel;
        this.statusClass = statusClass;
        this.createdAt = createdAt;
    }

    public Long getAdminId() {
        return adminId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRoleLabel() {
        return roleLabel;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
