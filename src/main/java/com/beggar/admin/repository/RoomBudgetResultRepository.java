package com.beggar.admin.repository;

import com.beggar.admin.entity.RoomBudgetResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomBudgetResultRepository extends JpaRepository<RoomBudgetResult, Long> {

    Optional<RoomBudgetResult> findByRoomNo(Long roomNo);
}
