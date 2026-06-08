package com.beggar.admin.repository;

import com.beggar.admin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findTop5ByOrderByCreatedAtDesc();

    Page<User> findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String userName,
            String email,
            Pageable pageable
    );
}
