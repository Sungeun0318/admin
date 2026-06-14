package com.beggar.admin.service;

import com.beggar.admin.dto.DashboardListItem;
import com.beggar.admin.dto.DashboardStats;
import com.beggar.admin.repository.ReceiptRepository;
import com.beggar.admin.repository.RoomFreeChatRepository;
import com.beggar.admin.repository.RoomFreeCommentRepository;
import com.beggar.admin.repository.RoomFreePostRepository;
import com.beggar.admin.repository.RoomRepository;
import com.beggar.admin.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DashboardService {

    private static final List<String> IN_PROGRESS_ROOM_STATUSES =
            List.of("INVITING", "BUDGET_INPUT", "BUDGET_DONE", "ACTIVE");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomFreePostRepository postRepository;
    private final RoomFreeCommentRepository commentRepository;
    private final RoomFreeChatRepository chatRepository;
    private final ReceiptRepository receiptRepository;

    public DashboardService(
            UserRepository userRepository,
            RoomRepository roomRepository,
            RoomFreePostRepository postRepository,
            RoomFreeCommentRepository commentRepository,
            RoomFreeChatRepository chatRepository,
            ReceiptRepository receiptRepository
    ) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.chatRepository = chatRepository;
        this.receiptRepository = receiptRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStats getStats() {
        long totalRooms = roomRepository.count();

        return new DashboardStats(
                userRepository.count(),
                totalRooms,
                roomRepository.countByStatusIn(IN_PROGRESS_ROOM_STATUSES),
                roomRepository.countByStatus("ENDED"),
                roomRepository.countByStatus("DELETED"),
                postRepository.count(),
                commentRepository.count(),
                chatRepository.count(),
                receiptRepository.count()
        );
    }

    @Transactional(readOnly = true)
    public List<DashboardListItem> getRecentUsers() {
        return userRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(user -> new DashboardListItem(
                        user.getUserName(),
                        user.getEmail(),
                        formatDateTime(user.getCreatedAt())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DashboardListItem> getRecentRooms() {
        return roomRepository.findTop5ByOrderByRoomCreatedDesc().stream()
                .map(room -> new DashboardListItem(
                        room.getRoomName(),
                        room.getLocation() == null ? "지역 미설정" : room.getLocation(),
                        formatDateTime(room.getRoomCreated())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DashboardListItem> getRecentPosts() {
        return postRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(post -> new DashboardListItem(
                        post.getTitle(),
                        post.getTag() == null ? "태그 없음" : post.getTag(),
                        formatDateTime(post.getCreatedAt())
                ))
                .toList();
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
}
