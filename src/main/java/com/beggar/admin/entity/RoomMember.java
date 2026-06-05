package com.beggar.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_members")
public class RoomMember {

    @Id
    @Column(name = "room_member_id")
    private Long roomMemberId;

    @Column(name = "room_no", insertable = false, updatable = false)
    private Long roomNo;

    @Column(name = "user_no", insertable = false, updatable = false)
    private Long userNo;

    @Column(name = "status")
    private String status;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    protected RoomMember() {
    }

    public Long getRoomMemberId() {
        return roomMemberId;
    }

    public Long getRoomNo() {
        return roomNo;
    }

    public Long getUserNo() {
        return userNo;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
