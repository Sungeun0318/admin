package com.beggar.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_free_chats")
public class RoomFreeChat {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "user_no", insertable = false, updatable = false)
    private Long userNo;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected RoomFreeChat() {
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getUserNo() {
        return userNo;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
