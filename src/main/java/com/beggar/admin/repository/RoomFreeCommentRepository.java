package com.beggar.admin.repository;

import com.beggar.admin.entity.RoomFreeComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomFreeCommentRepository extends JpaRepository<RoomFreeComment, Long> {

    long countByUserNo(Long userNo);

    long countByPostId(Long postId);

    List<RoomFreeComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    Page<RoomFreeComment> findByContentContainingIgnoreCase(String content, Pageable pageable);

    Page<RoomFreeComment> findByPostId(Long postId, Pageable pageable);

    Page<RoomFreeComment> findByPostIdAndContentContainingIgnoreCase(Long postId, String content, Pageable pageable);

    @Modifying
    @Query("delete from RoomFreeComment c where c.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
