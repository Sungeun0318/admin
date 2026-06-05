package com.beggar.admin.repository;

import com.beggar.admin.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    long countByRoomNo(Long roomNo);

    @Query("select coalesce(sum(r.amount), 0) from Receipt r where r.roomNo = :roomNo")
    long sumAmountByRoomNo(@Param("roomNo") Long roomNo);
}
