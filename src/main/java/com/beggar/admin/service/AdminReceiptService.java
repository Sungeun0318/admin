package com.beggar.admin.service;

import com.beggar.admin.dto.ReceiptDetail;
import com.beggar.admin.dto.ReceiptListItem;
import com.beggar.admin.entity.Receipt;
import com.beggar.admin.entity.Room;
import com.beggar.admin.entity.RoomMember;
import com.beggar.admin.entity.User;
import com.beggar.admin.repository.ReceiptRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Service
public class AdminReceiptService {

    private static final int PAGE_SIZE = 10;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    private static final NumberFormat MONEY_FORMATTER = NumberFormat.getNumberInstance(Locale.KOREA);

    private final ReceiptRepository receiptRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;
    private final AdminActionLogService actionLogService;

    public AdminReceiptService(
            ReceiptRepository receiptRepository,
            RoomRepository roomRepository,
            RoomMemberRepository roomMemberRepository,
            UserRepository userRepository,
            AdminActionLogService actionLogService
    ) {
        this.receiptRepository = receiptRepository;
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.userRepository = userRepository;
        this.actionLogService = actionLogService;
    }

    @Transactional(readOnly = true)
    public Page<ReceiptListItem> getReceipts(
            String keyword,
            Long roomNo,
            Long roomMemberId,
            LocalDate fromDate,
            LocalDate toDate,
            int page
    ) {
        int safePage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(
                safePage,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        String trimmed = keyword == null ? "" : keyword.trim();
        LocalDateTime fromDateTime = fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate == null ? null : toDate.plusDays(1).atStartOfDay();

        Page<Receipt> receipts = receiptRepository.searchReceipts(
                trimmed,
                roomNo,
                roomMemberId,
                fromDateTime,
                toDateTime,
                pageable
        );

        return new PageImpl<>(
                receipts.getContent().stream().map(this::toListItem).toList(),
                pageable,
                receipts.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public ReceiptDetail getReceiptDetail(Long receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new IllegalArgumentException("영수증을 찾을 수 없습니다."));

        return new ReceiptDetail(
                receipt.getReceiptId(),
                roomLabel(receipt.getRoomNo()),
                uploaderLabel(receipt.getRoomMemberId()),
                receiptTypeLabel(receipt.getReceiptType()),
                inputMethodLabel(receipt.getInputMethod()),
                blankToDash(receipt.getImageUrl()),
                ocrStatusLabel(receipt.getOcrStatus()),
                blankToDash(receipt.getStoreName()),
                money(receipt.getTotalAmount()),
                money(receipt.getAmount()),
                blankToDash(receipt.getAddress()),
                goodPriceMatchedLabel(receipt.getGoodPriceMatched()),
                blankToDash(receipt.getGoodPriceStoreId()),
                blankToDash(receipt.getGoodPriceStoreName()),
                blankToDash(receipt.getGoodPriceStoreAddress()),
                formatDateTime(receipt.getGoodPriceVerifiedAt()),
                formatDateTime(receipt.getCreatedAt())
        );
    }

    @Transactional
    public void deleteReceipt(Long receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new IllegalArgumentException("영수증을 찾을 수 없습니다."));
        receiptRepository.deleteById(receiptId);
        actionLogService.record("DELETE", "RECEIPT", receiptId, "영수증을 삭제했습니다. 방 #" + receipt.getRoomNo());
    }

    private ReceiptListItem toListItem(Receipt receipt) {
        return new ReceiptListItem(
                receipt.getReceiptId(),
                roomLabel(receipt.getRoomNo()),
                uploaderLabel(receipt.getRoomMemberId()),
                blankToDash(receipt.getStoreName()),
                receiptTypeLabel(receipt.getReceiptType()),
                inputMethodLabel(receipt.getInputMethod()),
                ocrStatusLabel(receipt.getOcrStatus()),
                money(receipt.getAmount()),
                goodPriceMatchedLabel(receipt.getGoodPriceMatched()),
                formatDateTime(receipt.getCreatedAt())
        );
    }

    private String roomLabel(Long roomNo) {
        if (roomNo == null) {
            return "-";
        }

        return roomRepository.findById(roomNo)
                .map(this::roomLabel)
                .orElse("방 #" + roomNo);
    }

    private String roomLabel(Room room) {
        return "%s (#%d)".formatted(blankToDash(room.getRoomName()), room.getRoomNo());
    }

    private String uploaderLabel(Long roomMemberId) {
        if (roomMemberId == null) {
            return "-";
        }

        Optional<RoomMember> roomMember = roomMemberRepository.findById(roomMemberId);
        if (roomMember.isEmpty()) {
            return "멤버 #" + roomMemberId;
        }

        Long userNo = roomMember.get().getUserNo();
        if (userNo == null) {
            return "멤버 #" + roomMemberId;
        }

        return userRepository.findById(userNo)
                .map(this::userLabel)
                .orElse("회원 #" + userNo);
    }

    private String userLabel(User user) {
        return "%s (#%d)".formatted(blankToDash(user.getUserName()), user.getUserNo());
    }

    private String receiptTypeLabel(String value) {
        if ("SPLIT".equals(value)) {
            return "분할";
        }
        if ("COMBINED".equals(value)) {
            return "통합";
        }
        return blankToDash(value);
    }

    private String inputMethodLabel(String value) {
        return switch (value == null ? "" : value) {
            case "CAMERA" -> "촬영";
            case "GALLERY" -> "갤러리";
            case "MANUAL" -> "수동";
            default -> blankToDash(value);
        };
    }

    private String ocrStatusLabel(String value) {
        return switch (value == null ? "" : value) {
            case "PENDING" -> "대기";
            case "SUCCESS" -> "성공";
            case "FAILED" -> "실패";
            case "CANCELED" -> "취소";
            case "MANUAL" -> "수동";
            default -> blankToDash(value);
        };
    }

    private String goodPriceMatchedLabel(Boolean matched) {
        return Boolean.TRUE.equals(matched) ? "매칭" : "미매칭";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime
                .atZone(java.time.ZoneId.of("UTC"))
                .withZoneSameInstant(java.time.ZoneId.of("Asia/Seoul"))
                .format(DATE_TIME_FORMATTER);
    }

    private String money(Integer value) {
        if (value == null) {
            return "-";
        }
        return MONEY_FORMATTER.format(value) + "원";
    }

    private String blankToDash(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value;
    }
}
