package com.beggar.admin.repository;

import com.beggar.admin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    long countByCreatedAtGreaterThanEqual(LocalDateTime createdAt);

    List<User> findTop5ByOrderByCreatedAtDesc();

    Page<User> findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String userName,
            String email,
            Pageable pageable
    );
}
