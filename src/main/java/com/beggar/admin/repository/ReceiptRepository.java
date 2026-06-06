package com.beggar.admin.repository;

import com.beggar.admin.entity.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    long countByRoomNo(Long roomNo);

    @Query("select coalesce(sum(r.amount), 0) from Receipt r where r.roomNo = :roomNo")
    long sumAmountByRoomNo(@Param("roomNo") Long roomNo);

    @Query("""
            SELECT r
              FROM Receipt r
             WHERE (:roomNo IS NULL OR r.roomNo = :roomNo)
               AND (:roomMemberId IS NULL OR r.roomMemberId = :roomMemberId)
               AND (:fromDate IS NULL OR r.createdAt >= :fromDate)
               AND (:toDate IS NULL OR r.createdAt < :toDate)
               AND (
                    :keyword = ''
                    OR LOWER(COALESCE(r.storeName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(r.address, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(r.goodPriceStoreName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               )
            """)
    Page<Receipt> searchReceipts(
            @Param("keyword") String keyword,
            @Param("roomNo") Long roomNo,
            @Param("roomMemberId") Long roomMemberId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );
}
