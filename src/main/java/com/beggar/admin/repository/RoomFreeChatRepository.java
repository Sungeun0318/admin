package com.beggar.admin.repository;

import com.beggar.admin.entity.RoomFreeChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomFreeChatRepository extends JpaRepository<RoomFreeChat, Long> {

    Page<RoomFreeChat> findByMessageContainingIgnoreCase(String message, Pageable pageable);

    Page<RoomFreeChat> findByUserNo(Long userNo, Pageable pageable);

    Page<RoomFreeChat> findByUserNoAndMessageContainingIgnoreCase(Long userNo, String message, Pageable pageable);
}
