package com.beggar.admin.controller;

import com.beggar.admin.dto.CommentListItem;
import com.beggar.admin.service.AdminCommentService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    public AdminCommentController(AdminCommentService adminCommentService) {
        this.adminCommentService = adminCommentService;
    }

    @GetMapping("/admin/community/comments")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long postId,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<CommentListItem> comments = adminCommentService.getComments(keyword, postId, page);

        model.addAttribute("pageTitle", "댓글 관리");
        model.addAttribute("pageDescription", "커뮤니티 댓글을 검색하고 삭제할 수 있어.");
        model.addAttribute("activeMenu", "comments");
        model.addAttribute("comments", comments);
        model.addAttribute("keyword", keyword);
        model.addAttribute("postId", postId);
        return "community/comments";
    }

    @PostMapping("/admin/community/comments/delete")
    public String delete(
            @RequestParam Long commentId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long postId,
            RedirectAttributes redirectAttributes
    ) {
        adminCommentService.deleteComment(commentId);
        redirectAttributes.addFlashAttribute("message", "댓글을 삭제했어.");
        String postParam = postId == null ? "" : "&postId=" + postId;
        return "redirect:/admin/community/comments?keyword=" + keyword + postParam;
    }
}
