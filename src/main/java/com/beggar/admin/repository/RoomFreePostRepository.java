package com.beggar.admin.repository;

import com.beggar.admin.entity.RoomFreePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomFreePostRepository extends JpaRepository<RoomFreePost, Long> {

    List<RoomFreePost> findTop5ByOrderByCreatedAtDesc();

    long countByUserNo(Long userNo);

    Page<RoomFreePost> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title,
            String content,
            Pageable pageable
    );

    Page<RoomFreePost> findByTagAndTitleContainingIgnoreCaseOrTagAndContentContainingIgnoreCase(
            String titleTag,
            String title,
            String contentTag,
            String content,
            Pageable pageable
    );
}
