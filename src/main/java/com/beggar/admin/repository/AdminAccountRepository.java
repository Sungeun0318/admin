package com.beggar.admin.repository;

import com.beggar.admin.entity.AdminAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminAccountRepository extends JpaRepository<AdminAccount, Long> {

    Optional<AdminAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("""
            SELECT a
              FROM AdminAccount a
             WHERE (:status = 'ALL' OR a.status = :status)
               AND (
                    :keyword = ''
                    OR LOWER(COALESCE(a.username, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(a.displayName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               )
            """)
    Page<AdminAccount> searchAccounts(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );
}
