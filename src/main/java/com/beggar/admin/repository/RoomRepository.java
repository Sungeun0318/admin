package com.beggar.admin.repository;

import com.beggar.admin.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    long countByRoomCreatedGreaterThanEqual(LocalDateTime roomCreated);

    long countByStatus(String status);

    List<Room> findTop5ByOrderByRoomCreatedDesc();

    long countByOwnerUserNo(Long ownerUserNo);

    @Query("""
            SELECT r
              FROM Room r
             WHERE (:status = 'ALL' OR r.status = :status)
               AND (
                    :keyword = ''
                    OR LOWER(COALESCE(r.roomName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(r.location, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(r.roomCode, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               )
            """)
    Page<Room> searchRooms(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );
}
