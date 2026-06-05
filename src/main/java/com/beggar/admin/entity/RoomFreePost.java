package com.beggar.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_free_posts")
public class RoomFreePost {

    @Id
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "tag")
    private String tag;

    @Column(name = "user_no", insertable = false, updatable = false)
    private Long userNo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected RoomFreePost() {
    }

    public Long getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTag() {
        return tag;
    }

    public Long getUserNo() {
        return userNo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
