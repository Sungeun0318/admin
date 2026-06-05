package com.beggar.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @Column(name = "room_no")
    private Long roomNo;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "room_code")
    private String roomCode;

    @Column(name = "owner_user_no")
    private Long ownerUserNo;

    @Column(name = "max_member_count")
    private int maxMemberCount;

    @Column(name = "location")
    private String location;

    @Column(name = "room_created")
    private LocalDateTime roomCreated;

    @Column(name = "status")
    private String status;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Room() {
    }

    public Long getRoomNo() {
        return roomNo;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public Long getOwnerUserNo() {
        return ownerUserNo;
    }

    public int getMaxMemberCount() {
        return maxMemberCount;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getRoomCreated() {
        return roomCreated;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void markEnded(LocalDateTime endedAt) {
        this.status = "ENDED";
        this.endedAt = endedAt;
    }

    public void markDeleted(LocalDateTime deletedAt) {
        this.status = "DELETED";
        this.deletedAt = deletedAt;
    }
}
