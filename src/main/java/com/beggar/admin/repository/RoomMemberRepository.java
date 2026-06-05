package com.beggar.admin.repository;

import com.beggar.admin.entity.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

    long countByUserNo(Long userNo);

    long countByRoomNo(Long roomNo);

    long countByRoomNoAndStatus(Long roomNo, String status);
}
