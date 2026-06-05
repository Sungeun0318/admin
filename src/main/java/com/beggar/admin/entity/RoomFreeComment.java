package com.beggar.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "room_free_comments")
public class RoomFreeComment {

    @Id
    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "post_id", insertable = false, updatable = false)
    private Long postId;

    @Column(name = "content")
    private String content;

    @Column(name = "user_no", insertable = false, updatable = false)
    private Long userNo;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    protected RoomFreeComment() {
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getUserNo() {
        return userNo;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
