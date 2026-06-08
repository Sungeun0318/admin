package com.beggar.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_account")
public class AdminAccount {

    public enum Role {
        SUPER_ADMIN,
        OPERATOR,
        VIEWER
    }

    public enum Status {
        ACTIVE,
        DISABLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "username")
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "display_name")
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected AdminAccount() {
    }

    public AdminAccount(
            String username,
            String passwordHash,
            String displayName,
            Role role,
            Status status
    ) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.role = role == null ? Role.VIEWER : role;
        this.status = status == null ? Status.ACTIVE : status;
        this.createdAt = LocalDateTime.now();
    }

    public void updateProfile(String displayName, Role role, Status status) {
        this.displayName = displayName;
        this.role = role == null ? Role.VIEWER : role;
        this.status = status == null ? Status.ACTIVE : status;
    }

    public void updatePassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void disable() {
        this.status = Status.DISABLED;
    }

    public Long getAdminId() {
        return adminId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Role getRole() {
        return role;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
