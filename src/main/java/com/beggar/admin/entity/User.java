package com.beggar.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_no")
    private Long userNo;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "uemail")
    private String email;

    @Column(name = "role")
    private String role;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "age_range")
    private String ageRange;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected User() {
    }

    public Long getUserNo() {
        return userNo;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Integer getGender() {
        return gender;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
