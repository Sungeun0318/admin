package com.beggar.admin.service;

import com.beggar.admin.dto.CommentListItem;
import com.beggar.admin.entity.RoomFreeComment;
import com.beggar.admin.entity.RoomFreePost;
import com.beggar.admin.entity.User;
import com.beggar.admin.repository.RoomFreeCommentRepository;
import com.beggar.admin.repository.RoomFreePostRepository;
import com.beggar.admin.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdminCommentService {

    private static final int PAGE_SIZE = 10;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    private final RoomFreeCommentRepository commentRepository;
    private final RoomFreePostRepository postRepository;
    private final UserRepository userRepository;
    private final AdminActionLogService actionLogService;

    public AdminCommentService(
            RoomFreeCommentRepository commentRepository,
            RoomFreePostRepository postRepository,
            UserRepository userRepository,
            AdminActionLogService actionLogService
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.actionLogService = actionLogService;
    }

    @Transactional(readOnly = true)
    public Page<CommentListItem> getComments(String keyword, Long postId, int page) {
        int safePage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(
                safePage,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        String trimmed = keyword == null ? "" : keyword.trim();

        Page<RoomFreeComment> comments;
        if (postId != null && !trimmed.isEmpty()) {
            comments = commentRepository.findByPostIdAndContentContainingIgnoreCase(postId, trimmed, pageable);
        } else if (postId != null) {
            comments = commentRepository.findByPostId(postId, pageable);
        } else if (!trimmed.isEmpty()) {
            comments = commentRepository.findByContentContainingIgnoreCase(trimmed, pageable);
        } else {
            comments = commentRepository.findAll(pageable);
        }

        return new PageImpl<>(
                comments.getContent().stream().map(this::toListItem).toList(),
                pageable,
                comments.getTotalElements()
        );
    }

    @Transactional
    public void deleteComment(Long commentId) {
        RoomFreeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없어."));
        commentRepository.deleteById(commentId);
        actionLogService.record("DELETE", "COMMENT", commentId, "댓글을 삭제했어. 게시글 #" + comment.getPostId());
    }

    private CommentListItem toListItem(RoomFreeComment comment) {
        return new CommentListItem(
                comment.getCommentId(),
                comment.getPostId(),
                postTitle(comment.getPostId()),
                authorLabel(comment.getUserNo()),
                comment.getContent(),
                formatDateTime(comment.getCreatedAt())
        );
    }

    private String postTitle(Long postId) {
        if (postId == null) {
            return "-";
        }
        return postRepository.findById(postId)
                .map(RoomFreePost::getTitle)
                .orElse("게시글 #" + postId);
    }

    private String authorLabel(Long userNo) {
        if (userNo == null) {
            return "-";
        }
        return userRepository.findById(userNo)
                .map(this::userLabel)
                .orElse("회원 #" + userNo);
    }

    private String userLabel(User user) {
        return "%s (#%d)".formatted(user.getUserName(), user.getUserNo());
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
