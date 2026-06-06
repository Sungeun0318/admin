package com.beggar.admin.service;

import com.beggar.admin.dto.RoomDetail;
import com.beggar.admin.dto.RoomListItem;
import com.beggar.admin.entity.Room;
import com.beggar.admin.entity.RoomBudgetResult;
import com.beggar.admin.entity.User;
import com.beggar.admin.repository.ReceiptRepository;
import com.beggar.admin.repository.RoomBudgetResultRepository;
import com.beggar.admin.repository.RoomMemberRepository;
import com.beggar.admin.repository.RoomRepository;
import com.beggar.admin.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Service
public class AdminRoomService {

    private static final int PAGE_SIZE = 10;
    private static final String STATUS_ALL = "ALL";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_ENDED = "ENDED";
    private static final String STATUS_DELETED = "DELETED";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    private static final NumberFormat MONEY_FORMATTER = NumberFormat.getNumberInstance(Locale.KOREA);

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomBudgetResultRepository budgetResultRepository;
    private final ReceiptRepository receiptRepository;
    private final AdminActionLogService actionLogService;

    public AdminRoomService(
            RoomRepository roomRepository,
            UserRepository userRepository,
            RoomMemberRepository roomMemberRepository,
            RoomBudgetResultRepository budgetResultRepository,
            ReceiptRepository receiptRepository,
            AdminActionLogService actionLogService
    ) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.budgetResultRepository = budgetResultRepository;
        this.receiptRepository = receiptRepository;
        this.actionLogService = actionLogService;
    }

    @Transactional(readOnly = true)
    public Page<RoomListItem> getRooms(String keyword, String status, int page) {
        int safePage = Math.max(page, 0);
        String safeStatus = normalizeStatus(status);
        Pageable pageable = PageRequest.of(
                safePage,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "roomCreated")
        );

        String trimmed = keyword == null ? "" : keyword.trim();
        Page<Room> rooms = roomRepository.searchRooms(trimmed, safeStatus, pageable);

        return new PageImpl<>(
                rooms.getContent().stream().map(this::toListItem).toList(),
                pageable,
                rooms.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public RoomDetail getRoomDetail(Long roomNo) {
        Room room = roomRepository.findById(roomNo)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없어."));
        Optional<RoomBudgetResult> budgetResult = budgetResultRepository.findByRoomNo(roomNo);

        return new RoomDetail(
                room.getRoomNo(),
                room.getRoomName(),
                room.getRoomCode(),
                ownerLabel(room.getOwnerUserNo()),
                blankToDash(room.getLocation()),
                room.getMaxMemberCount(),
                normalizeStatus(room.getStatus()),
                formatDateTime(room.getRoomCreated()),
                formatDateTime(room.getEndedAt()),
                formatDateTime(room.getDeletedAt()),
                roomMemberRepository.countByRoomNo(roomNo),
                roomMemberRepository.countByRoomNoAndStatus(roomNo, "ACTIVE"),
                budgetResult.map(result -> money(result.getMinBudgetPerPerson()) + "원").orElse("-"),
                budgetResult.map(result -> money(result.getTotalBudget()) + "원").orElse("-"),
                budgetResult.map(result -> formatDateTime(result.getConfirmedAt())).orElse("-"),
                receiptRepository.countByRoomNo(roomNo),
                money(receiptRepository.sumAmountByRoomNo(roomNo)) + "원"
        );
    }

    @Transactional
    public void endRoom(Long roomNo) {
        Room room = roomRepository.findById(roomNo)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없어."));
        String status = normalizeStatus(room.getStatus());

        if (STATUS_DELETED.equals(status)) {
            throw new IllegalStateException("이미 삭제된 방은 종료할 수 없어.");
        }
        if (!STATUS_ENDED.equals(status)) {
            room.markEnded(LocalDateTime.now());
            actionLogService.record("END", "ROOM", roomNo, "방을 강제 종료했어: " + room.getRoomName());
        }
    }

    @Transactional
    public void deleteRoom(Long roomNo) {
        Room room = roomRepository.findById(roomNo)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없어."));

        if (!STATUS_DELETED.equals(normalizeStatus(room.getStatus()))) {
            room.markDeleted(LocalDateTime.now());
            actionLogService.record("DELETE", "ROOM", roomNo, "방을 삭제 처리했어: " + room.getRoomName());
        }
    }

    private RoomListItem toListItem(Room room) {
        return new RoomListItem(
                room.getRoomNo(),
                room.getRoomName(),
                room.getRoomCode(),
                ownerLabel(room.getOwnerUserNo()),
                blankToDash(room.getLocation()),
                normalizeStatus(room.getStatus()),
                formatDateTime(room.getRoomCreated())
        );
    }

    private String normalizeStatus(String status) {
        if (STATUS_ACTIVE.equals(status) || STATUS_ENDED.equals(status) || STATUS_DELETED.equals(status)) {
            return status;
        }
        if (STATUS_ALL.equals(status)) {
            return STATUS_ALL;
        }
        return STATUS_ACTIVE;
    }

    private String ownerLabel(Long ownerUserNo) {
        if (ownerUserNo == null) {
            return "-";
        }

        return userRepository.findById(ownerUserNo)
                .map(this::userLabel)
                .orElse("회원 #" + ownerUserNo);
    }

    private String userLabel(User user) {
        return "%s (#%d)".formatted(user.getUserName(), user.getUserNo());
    }

    private String blankToDash(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    private String money(Integer value) {
        if (value == null) {
            return "-";
        }
        return money(value.longValue());
    }

    private String money(long value) {
        return MONEY_FORMATTER.format(value);
    }
}
